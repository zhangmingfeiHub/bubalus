/**
 * @author mingfei.z
 */
package com.shuhang.bubalus.db;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * 替代 org.mybatis.spring.mapper.MapperFactoryBean
 * @param <T>
 * @author mingfei.z
 */
public class MapperFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Class<T> mapperInterface;
	private boolean addToConfig = true;
	
	public MapperFactoryBean() { }
	
	public MapperFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}
	
	@Override
	public T getObject() throws Exception {
		return getSqlSession().getMapper(this.mapperInterface);
	}

	@Override
	public Class<?> getObjectType() {
		return this.mapperInterface;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		super.checkDaoConfig();
		
		Assert.notNull(this.mapperInterface, "Property 'mapperInterface' is required");
		
		Configuration configuration = getSqlSession().getConfiguration();
		if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
			try {
				configuration.addMapper(this.mapperInterface);
			} catch (Exception e) {
				logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", e);
				throw new IllegalArgumentException(e);
			} finally {
				ErrorContext.instance().reset();
			}
		}
	}
	
	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}
	public Class<T> getMapperInterface() {
		return this.mapperInterface;
	}
	
	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}
	public boolean isAddToConfig() {
		return this.addToConfig;
	}

}
