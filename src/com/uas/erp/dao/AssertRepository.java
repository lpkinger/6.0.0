package com.uas.erp.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.uas.erp.core.support.Assert;

/**
 * 断言
 * 
 * @author yingp
 * 
 * @param <T>
 */
public class AssertRepository<T extends JdbcDaoSupport> {

	private T dao;

	public AssertRepository(T dao) {
		this.dao = dao;
	}

	/**
	 * 断言：按条件一定能查找到结果，否则抛出异常
	 * 
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件
	 * @param message
	 *            异常信息
	 */
	public void isExist(String tableName, String condition, String message) {
		int count = dao.getJdbcTemplate().queryForObject("select count(1) from " + tableName + " where " + condition, Integer.class);
		Assert.isTrue(count > 0, message);
	}

	/**
	 * 断言：按条件一定能查找到结果，否则抛出异常
	 * 
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件
	 * @param message
	 *            异常信息
	 */
	public void isTrue(String tableName, String condition, String message) {
		isExist(tableName, condition, message);
	}

	/**
	 * 断言：按条件查找到的结果一定为空，否则抛出异常
	 * 
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件
	 * @param message
	 *            异常信息
	 */
	public void notExist(String tableName, String condition, String message) {
		int count = dao.getJdbcTemplate().queryForObject("select count(1) from " + tableName + " where " + condition, Integer.class);
		Assert.isTrue(count == 0, message);
	}

	/**
	 * 断言：按条件查找到的结果一定为空，否则抛出异常
	 * 
	 * @param tableName
	 *            表名
	 * @param condition
	 *            查询条件
	 * @param message
	 *            异常信息
	 */
	public void isFalse(String tableName, String condition, String message) {
		notExist(tableName, condition, message);
	}

	/**
	 * 断言：编号不存在，否则抛出异常{common.save_codeHasExist}
	 * 
	 * @param tableName
	 *            表名
	 * @param codeField
	 *            编号字段
	 * @param codeValue
	 *            编号
	 */
	public void nonExistCode(String tableName, String codeField, Object codeValue) {
		notExist(tableName, codeField + "='" + codeValue + "'", "common.save_codeHasExist");
	}

}
