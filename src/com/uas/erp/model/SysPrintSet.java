package com.uas.erp.model;

import java.io.Serializable;


public class SysPrintSet  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String  caller;
	private String reportname;
	private String title;
	private Integer isdefault;//默认
	private Integer needaudit;//已审核才能打印
	private Integer  nopost;//已过帐不允许打印
	private Integer  needenoughstock;//库存不足不允许打印
	private String  countfield;//打印次数字段
	private String  statusfield;//状态字段
	private String  statuscodefield;//状态码字段
	private Integer  allowmultiple;//允许多次打印
	private String defaultcondition;//默认条件
	private String handlermethod; //打印前执行逻辑方法名
	private Integer id;
	private String printtype; //输出类型
	private String tablename;//表名
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getIsdefault() {
		return isdefault;
	}
	public void setIsdefault(Integer isdefault) {
		this.isdefault = isdefault;
	}
	public Integer getNeedaudit() {
		return needaudit;
	}
	public void setNeedaudit(Integer needaudit) {
		this.needaudit = needaudit;
	}
	public Integer getNopost() {
		return nopost;
	}
	public void setNopost(Integer nopost) {
		this.nopost = nopost;
	}
	public Integer getNeedenoughstock() {
		return needenoughstock;
	}
	public void setNeedenoughstock(Integer needenoughstock) {
		this.needenoughstock = needenoughstock;
	}
	public String getCountfield() {
		return countfield;
	}
	public void setCountfield(String countfield) {
		this.countfield = countfield;
	}
	public String getStatusfield() {
		return statusfield;
	}
	public void setStatusfield(String statusfield) {
		this.statusfield = statusfield;
	}
	public String getStatuscodefield() {
		return statuscodefield;
	}
	public void setStatuscodefield(String statuscodefield) {
		this.statuscodefield = statuscodefield;
	}
	public Integer getAllowmultiple() {
		return allowmultiple;
	}
	public void setAllowmultiple(Integer allowmultiple) {
		this.allowmultiple = allowmultiple;
	}
	public String getDefaultcondition() {
		return defaultcondition;
	}
	public void setDefaultcondition(String defaultcondition) {
		this.defaultcondition = defaultcondition;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getReportname() {
		return reportname;
	}
	public void setReportname(String reportname) {
		this.reportname = reportname;
	}
	public String getHandlermethod() {
		return handlermethod;
	}
	public void setHandlermethod(String handlermethod) {
		this.handlermethod = handlermethod;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPrinttype() {
		return printtype;
	}
	public void setPrinttype(String printtype) {
		this.printtype = printtype;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
}
