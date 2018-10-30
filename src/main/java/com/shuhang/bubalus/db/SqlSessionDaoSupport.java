/**
 * @author mingfei.z
 */
package com.shuhang.bubalus.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

public class SqlSessionDaoSupport extends DaoSupport {

	private SqlSession sqlSession;
	private boolean externalSqlSession;
	
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		Assert.notNull(this.sqlSession, "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
	}
	
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		if (!this.externalSqlSession) {
			//使用DynamicSqlSessionTemplate 实现读写分离
			this.sqlSession = new DynamicSqlSessionTemplate(new SqlSessionTemplate(sqlSessionFactory));
		}
	}
	
	/**
	 * 使用DynamicSqlSessionTemplate 实现读写分离
	 * 
	 * 
	 * @param sqlSessionTemplate DynamicSqlSessionTemplate
	 * @author mingfei.z
	 */
	public void setSqlSessionTemplate(DynamicSqlSessionTemplate sqlSessionTemplate) {
		this.sqlSession = sqlSessionTemplate;
		this.externalSqlSession = true;
	}
	
	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

}
