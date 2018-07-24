package com.uas.erp.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.uas.erp.dao.Saveable;

public class MessageLog implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ml_id;
	private Date ml_date;// 操作日期
	private String ml_man;// 操作人
	private String ml_content;// 内容
	private String ml_result;// 结果
	private String ml_search;// 用于事后查询单据操作记录时，用于检索的条件
	private String code; // 对应单号
	public int getMl_id() {
		return ml_id;
	}

	public void setMl_id(int ml_id) {
		this.ml_id = ml_id;
	}

	public Date getMl_date() {
		return ml_date;
	}

	public void setMl_date(Date ml_date) {
		this.ml_date = ml_date;
	}

	public String getMl_man() {
		return ml_man;
	}

	public void setMl_man(String ml_man) {
		this.ml_man = ml_man;
	}

	public String getMl_content() {
		return ml_content;
	}

	public void setMl_content(String ml_content) {
		this.ml_content = ml_content;
	}

	public String getMl_result() {
		return ml_result;
	}

	public void setMl_result(String ml_result) {
		this.ml_result = ml_result;
	}

	public String getMl_search() {
		return ml_search;
	}

	public void setMl_search(String ml_search) {
		this.ml_search = ml_search;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String table() {
		return "messagelog";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ml_id" };
	}

	public MessageLog() {

	}

	/**
	 * @param ml_man
	 *            操作人
	 * @param ml_content
	 *            内容
	 * @param ml_result
	 *            结果
	 * @param ml_search
	 *            用于事后查询单据操作记录时，用于检索的条件{caller|Key=keyValue}
	 */
	public MessageLog(String ml_man, String ml_content, String ml_result, String ml_search) {
		this.ml_man = ml_man;
		this.ml_content = ml_content;
		this.ml_result = ml_result;
		this.ml_search = ml_search;
	}

	public String getSql() {
		return getInsertSql(ml_man, ml_content, ml_result, ml_search);
	}

	public static String getInsertSql(String man, String content, String result, String search) {
		StringBuffer sql = new StringBuffer("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(");
		sql.append("to_date('");
		sql.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		sql.append("','yyyy-MM-dd HH24:mi:ss'),'");
		sql.append(man + "','");
		sql.append(content + "','");
		sql.append(result + "','");
		sql.append(search + "')");
		return sql.toString();
	}
}
