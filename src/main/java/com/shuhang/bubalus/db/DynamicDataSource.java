/**
 * @author mingfei.z
 */
package com.shuhang.bubalus.db;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 根据线程上下文来选择合适的数据源
 * 
 * @author mingfei.z
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private AtomicInteger counter = new AtomicInteger();
	private DataSource master;
	private List<DataSource> slaves;
	
	@Override
	protected Object determineCurrentLookupKey() {
		return null;
	}
	
	@Override
	public void afterPropertiesSet() {
		
	}
	
	@Override
	protected DataSource determineTargetDataSource() {

		DataSource returnDataSource = null;
		if (DataSourceHolder.isMaster()) {
			returnDataSource = master;
			logger.info("Master datasource have been chose");
		} else if (DataSourceHolder.isSlave()) {
			int count = counter.incrementAndGet();
			if (count > 1000000) {
				counter.set(0);
			}
			
			int n = slaves.size();
			int index = count % n;
			returnDataSource = slaves.get(index);
			logger.info("No.{} slave datasource have been chose", index);
		} else {
			returnDataSource = master;
			logger.info("Master datasource have been chose by default");
		}
		
		
		// TODO 
		/*if (returnDataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
			org.apache.tomcat.jdbc.pool.DataSource source = (org.apache.tomcat.jdbc.pool.DataSource) returnDataSource;
			String jdbcUrl = source.getUrl();
			logger.info("JdbcUrl:{}", jdbcUrl);
		}*/
		
		return super.determineTargetDataSource();
	}

}
