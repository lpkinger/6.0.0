package com.uas.erp.model;

import java.util.Date;

public class FlowDefine {
	private int fd_id;     
	private String fd_name;    //流程名称
	private String fd_versionnum;    //版本号
	private String fd_status;        //使用状态
	private String fd_shortname;     //版本简称
	private Date fd_date;            //创建时间
	private String fd_remark;        //备注
	private String fd_caller;        //form单据caller
	private int fd_detno;            //序号
	private String fd_man;           //责任人
	private int fd_parentid;         //父节点
	private String fd_isleaf;		 //是否为叶子节点  T,F
	private String fd_fcid;		 	 //关联flow_chart
	private String fd_defaultduty;		 	 //默认责任人
	private String fd_defaultdutycode;		 //默认责任人编号
	
	public String getFd_man() {
		return fd_man;
	}
	public void setFd_man(String fd_man) {
		this.fd_man = fd_man;
	}
	public int getFd_id() {
		return fd_id;
	}
	public void setFd_id(int fd_id) {
		this.fd_id = fd_id;
	}
	public String getFd_name() {
		return fd_name;
	}
	public void setFd_name(String fd_name) {
		this.fd_name = fd_name;
	}
	public String getFd_versionnum() {
		return fd_versionnum;
	}
	public void setFd_versionnum(String fd_versionnum) {
		this.fd_versionnum = fd_versionnum;
	}
	public String getFd_status() {
		return fd_status;
	}
	public void setFd_status(String fd_status) {
		this.fd_status = fd_status;
	}
	public String getFd_shortname() {
		return fd_shortname;
	}
	public void setFd_shortname(String fd_shortname) {
		this.fd_shortname = fd_shortname;
	}
	public Date getFd_date() {
		return fd_date;
	}
	public void setFd_date(Date fd_date) {
		this.fd_date = fd_date;
	}
	public String getFd_remark() {
		return fd_remark;
	}
	public void setFd_remark(String fd_remark) {
		this.fd_remark = fd_remark;
	}
	public String getFd_caller() {
		return fd_caller;
	}
	public void setFd_caller(String fd_caller) {
		this.fd_caller = fd_caller;
	}
	public int getFd_detno() {
		return fd_detno;
	}
	public void setFd_detno(int fd_detno) {
		this.fd_detno = fd_detno;
	}
	public int getFd_parentid() {
		return fd_parentid;
	}
	public void setFd_parentid(int fd_parentid) {
		this.fd_parentid = fd_parentid;
	}
	public String getFd_isleaf() {
		return fd_isleaf;
	}
	public void setFd_isleaf(String fd_isleaf) {
		this.fd_isleaf = fd_isleaf;
	}
	public String getFd_fcid() {
		return fd_fcid;
	}
	public void setFd_fcid(String fd_fcid) {
		this.fd_fcid = fd_fcid;
	}
	public String getFd_defaultduty() {
		return fd_defaultduty;
	}
	public void setFd_defaultduty(String fd_defaultduty) {
		this.fd_defaultduty = fd_defaultduty;
	}
	public String getFd_defaultdutycode() {
		return fd_defaultdutycode;
	}
	public void setFd_defaultdutycode(String fd_defaultdutycode) {
		this.fd_defaultdutycode = fd_defaultdutycode;
	}
	
}
