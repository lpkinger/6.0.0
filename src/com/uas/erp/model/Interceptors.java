package com.uas.erp.model;

import java.io.Serializable;

/**
 * 配置逻辑
 * 
 * @author yingp
 * 
 */
public class Interceptors implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final short BEFORE = 0;
	public static final short AFTER = 1;
	private int id;
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
	private String help;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
     
	public boolean isCommon_config() {
		return "com.uas.erp.service.common.impl.CommonHandler".equals(this.class_)&&"exec_handler".equals(this.method);
	}
}
