/**
 * @author mingfei.z
 */
package com.shuhang.bubalus.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.shuhang.bubalus.utils.JsonUtils;

/**
 * 未知<br/>
 * 替代 {@link SqlSessionTemplate}
 * 
 * @author mingfei.z
 */
public class DynamicSqlSessionTemplate implements SqlSession {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String SELECT = "select";
	private static final String INSERT = "insert";
	private static final String DELETE = "delete";
	private static final String UPDATE = "update";
	
	private SqlSessionTemplate sqlSessionTemplate;
	private final SqlSession sqlSessionProxy;
	
	public DynamicSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
		this.sqlSessionProxy = (SqlSession) Proxy.newProxyInstance(
				SqlSessionFactory.class.getClassLoader(), new Class[]{SqlSession.class}, new SqlSessionInterceptor());
	}
	
	/**
	 * 拦截SqlSessionTemplate的方法,从而进行读写分离 <br/>
	 * 注意:如果有事务,事务的入口已经选择了数据源,所以不需要做任何处理,非事务方法数据源的选择在此处完成.
	 * 
	 * @author mingfei.z
	 */
	private class SqlSessionInterceptor implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
			
			if (synchronizationActive) {
				// 此处不try的原因是DynamicDataSourceTransactionManager的doCleanupAfterCompletion会清空threadlocal
				return method.invoke(sqlSessionTemplate, args);
			} else {
				String methodName = method.getName();
				if (methodName.startsWith(SELECT)) {
					MappedStatement ms = getConfiguration().getMappedStatement((String) args[0]);
					
					// 此处只是为了拿SQL语句,并不是获取执行的BoundSql
					BoundSql boundSql = null;
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (args.length > 1 && parameterTypes[1].getName().equals("java.lang.Object")) {
						boundSql = ms.getBoundSql(args[1]);
					} else {
						boundSql = ms.getBoundSql(null);
					}
					
					String sql = boundSql.getSql();
					if (sql.startsWith("/*master*/")) {
						logger.info("Master database is selected by hint");
						DataSourceHolder.setMaster();
					} else {
						logger.info("Slaver database is selected");
						DataSourceHolder.setSlave();
					}
				} else if (methodName.startsWith(INSERT) || 
						methodName.startsWith(UPDATE) || 
						methodName.startsWith(DELETE)) {
					logger.info("Master database is selected");
					
					// 获取主库数据源
					DataSourceHolder.setMaster();
				}
				
				Object result;
				try {
					result = method.invoke(sqlSessionTemplate, args);
				} catch (Exception e) {
					throw e;
				} finally {
					// 清理工作
					DataSourceHolder.clearDataSource();
				}
				
				return result;
			}
		}
	}
	
	@Override
	public void clearCache() {
		sqlSessionProxy.clearCache();
	}

	@Override
	public void close() {
		sqlSessionProxy.close();
	}

	@Override
	public void commit() {
		sqlSessionProxy.commit();
	}

	@Override
	public void commit(boolean force) {
		sqlSessionProxy.commit(force);
	}

	@Override
	public int delete(String statement) {
		logger.info("delete statement: {}", statement);
		return sqlSessionProxy.delete(statement);
	}

	@Override
	public int delete(String statement, Object parameter) {
		logger.info("delete statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.delete(statement, parameter);
	}

	@Override
	public List<BatchResult> flushStatements() {
		return sqlSessionProxy.flushStatements();
	}

	@Override
	public Configuration getConfiguration() {
		return sqlSessionProxy.getConfiguration();
	}

	@Override
	public Connection getConnection() {
		return sqlSessionProxy.getConnection();
	}

	@Override
	public <T> T getMapper(Class<T> type) {
		return getConfiguration().getMapper(type, this);
	}

	@Override
	public int insert(String statement) {
		logger.info("insert statement: {}", statement);
		return sqlSessionProxy.insert(statement);
	}

	@Override
	public int insert(String statement, Object parameter) {
		logger.info("insert statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.insert(statement, parameter);
	}

	@Override
	public void rollback() {
		sqlSessionProxy.rollback();
	}

	@Override
	public void rollback(boolean force) {
		sqlSessionProxy.rollback(force);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(String statement, ResultHandler handler) {
		logger.info("select statement: {}", statement);
		sqlSessionProxy.select(statement, handler);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(String statement, Object parameter, ResultHandler handler) {
		logger.info("select statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		sqlSessionProxy.select(statement, parameter, handler);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
		logger.info("select 2 statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		sqlSessionProxy.select(statement, parameter, rowBounds, handler);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement) {
		logger.info("selectCursor statement: {}", statement);
		return sqlSessionProxy.selectCursor(statement);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement, Object parameter) {
		logger.info("selectCursor statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.selectCursor(statement, parameter);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
		logger.info("selectCursor 2 statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.selectCursor(statement, parameter, rowBounds);
	}

	@Override
	public <E> List<E> selectList(String statement) {
		logger.info("selectList statement: {}", statement);
		return sqlSessionProxy.selectList(statement);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		logger.info("selectList statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.selectList(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		logger.info("selectList 2 statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.selectList(statement, parameter, rowBounds);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		logger.info("selectMap statement: {}, mapKey: {}", statement, mapKey);
		return sqlSessionProxy.selectMap(statement, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		logger.info("selectMap statement: {}, parameter: {}, mapKey: {}", statement, JsonUtils.objectToJson(parameter, false), mapKey);
		return sqlSessionProxy.selectMap(statement, parameter, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		logger.info("selectMap 2 statement: {}, parameter: {}, mapKey: {}", statement, JsonUtils.objectToJson(parameter, false), mapKey);
		return sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
	}

	@Override
	public <T> T selectOne(String statement) {
		logger.info("selectOne statement: {}", statement);
		return sqlSessionProxy.selectOne(statement);
	}

	@Override
	public <T> T selectOne(String statement, Object parameter) {
		logger.info("selectOne statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.selectOne(statement, parameter);
	}

	@Override
	public int update(String statement) {
		logger.info("update statement: {}", statement);
		return sqlSessionProxy.update(statement);
	}

	@Override
	public int update(String statement, Object parameter) {
		logger.info("update statement: {}, parameter: {}", statement, JsonUtils.objectToJson(parameter, false));
		return sqlSessionProxy.update(statement, parameter);
	}

}
