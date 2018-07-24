package com.uas.erp.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import sun.security.action.GetLongAction;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Operation;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.ReadStatus;

/**
 * 记录日志工具
 * 
 * @author yingp
 * @see JdbcDaoSupport
 * @see BaseDao
 * 
 */
public class Logger<T extends BaseDao> {

	private T dao;

	public Logger(T dao) {
		this.dao = dao;
	}

	/**
	 * 记录操作
	 * 
	 * @param operation
	 *            操作类型
	 * @param search
	 *            用于查询的关键词
	 * @return
	 */
	private MessageLog getMessageLog(Operation operation, String search) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		return new MessageLog((employee == null ? "系统" : employee.getEm_name()),
				operation.getTitle(language), operation.getResult(language),
				search);
	}

	/**
	 * 记录操作
	 * 
	 * @param title
	 *            操作描述
	 * @param result
	 *            操作结果
	 * @param search
	 *            用于查询的关键词
	 * @return
	 */
	private MessageLog getMessageLog(String title, String result, String search) {
		Employee employee = SystemSession.getUser();
		return new MessageLog(employee.getEm_name(), title, result, search);
	}

	/**
	 * 记录操作
	 * 
	 * @param operation
	 *            操作类型
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 * @return
	 */
	public MessageLog getMessageLog(Operation operation, String caller,
			String searchKey, Object keyValue) {
		return getMessageLog(operation, caller + "|" + searchKey + "="
				+ keyValue);
	}

	/**
	 * 记录操作
	 * 
	 * @param title
	 *            操作描述
	 * @param result
	 *            操作结果
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 * @return
	 */
	public MessageLog getMessageLog(String title, String result,
			String caller, String searchKey, Object keyValue) {
		return getMessageLog(title, result, caller + "|" + searchKey + "="
				+ keyValue);
	}

	/**
	 * 记录新增操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void save(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.SAVE, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录修改操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void update(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.UPDATE, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录提交操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void submit(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.COMMIT, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录反提交操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void resSubmit(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.RESCOMMIT, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 * 记录禁用操作
	 * 
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void banned(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.BANNED, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录反禁用操作
	 * 
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void resBanned(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.RESBANNED, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 * 记录审核操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void audit(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.AUDIT, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录反审核操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void resAudit(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.RESAUDIT, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录记账操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void post(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.POST, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录反记账操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void resPost(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.RESPOST, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录删除操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void delete(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.DELETE, caller, searchKey, keyValue)
						.getSql());
	}

	/**
	 * 记录打印操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void print(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.PRINT, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 * 记录批准操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void approve(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.APPROVE, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 * 记录反批准操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void resApprove(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.RESAPPROVE, caller, searchKey, keyValue)
						.getSql());
	}
	
	/**
	 * 记录转单操作
	 * 
	 * @param title
	 *            操作描述
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void turn(String title, String caller, String searchKey,
			Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(BaseUtil.getLocalMessage(title),
						BaseUtil.getLocalMessage("msg.turnSuccess"), caller,
						searchKey, keyValue).getSql());
	}

	/**
	 * 记录转单操作
	 * 
	 * @param title
	 *            操作描述
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 * @param detailValue
	 *            明细行号
	 */
	public void turnDetail(String title, String caller, String searchKey,
			Object keyValue, Object detailValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(
						BaseUtil.getLocalMessage(title),
						BaseUtil.getLocalMessage("msg.turnSuccess") + ","
								+ BaseUtil.getLocalMessage("msg.detail")
								+ detailValue, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 * 记录结案操作
	 * 
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void end(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.FINISH, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 * 记录resEndSuccess操作
	 * 
	 * @param caller
	 *            界面caller
	 * 
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void resEnd(String caller, String searchKey, Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(Operation.RESFINISH, caller, searchKey, keyValue)
						.getSql());
	}
	/**
	 *记录 copy操作
	 *  @param caller 界面caller
	 *  @param codeValue 来源单号
	 *  @param searchKey 查询关键字段
	 *  @param keyValue  主键字段的值 
	 *  
	 * */
	public void copy(String caller,String codeValue,String searchKey,Object keyValue){      
		dao.getJdbcTemplate().execute(
				getMessageLog(BaseUtil.getLocalMessage("msg.Copy",SystemSession.getLang()), BaseUtil.getLocalMessage("msg.CopySuccess", 
						SystemSession.getLang())+codeValue,caller, searchKey, keyValue).getSql());
	}
	
	/**
	 * 记录其它操作
	 * 
	 * @param title
	 *            操作描述
	 * @param afterinfo
	 *            操作成功描述
	 * @param caller
	 *            界面caller
	 * @param searchKey
	 *            用于查询的关键字段
	 * @param keyValue
	 *            关键字段的值
	 */
	public void others(String title, String afterinfo, String caller, String searchKey,
			Object keyValue) {
		dao.getJdbcTemplate().execute(
				getMessageLog(BaseUtil.getLocalMessage(title),
						BaseUtil.getLocalMessage(afterinfo), caller,
						searchKey, keyValue).getSql());
	}
	/**
	 * 记录关于 新闻，通知...
	 * @param kind 
	 *   来源类型
	 * @param keyValue 
	 *   主键id
	 * 
	 * */
	public void read(String kind,Integer keyValue){
		dao.save(new ReadStatus(kind, keyValue));
	}
}
