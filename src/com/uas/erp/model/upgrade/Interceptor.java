package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的interceptors
 * 
 * @author yingp
 *
 */
public class Interceptor implements Saveable{

	private String caller;
	private String page_caller;
	private String title;
	private String class_;
	private String method;
	private String type;
	/**
	 * 0-before 1-after
	 */
	private Integer turn;
	private Integer detno;
	/**
	 * 0-disable 1-enable
	 */
	private Integer enable;
	
	private String plan_id;

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getPage_caller() {
		return page_caller;
	}

	public void setPage_caller(String page_caller) {
		this.page_caller = page_caller;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getClass_() {
		return class_;
	}

	public void setClass_(String class_) {
		this.class_ = class_;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getTurn() {
		return turn;
	}

	public void setTurn(Integer turn) {
		this.turn = turn;
	}

	public Integer getDetno() {
		return detno;
	}

	public void setDetno(Integer detno) {
		this.detno = detno;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	@JsonIgnore
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	@Override
	public String table() {
		return "upgrade$interceptors";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
