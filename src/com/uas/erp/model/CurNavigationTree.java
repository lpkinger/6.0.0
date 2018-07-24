package com.uas.erp.model;

public class CurNavigationTree {
	
	private int cn_id;  
	private String cn_title;    //导航名称
	private String cn_url;    //跳转地址
	private String cn_icon;     //图标
	private int cn_subof;     //父ID
	private int cn_isleaf;       //是否叶子节点
	private int cn_detno;       //序号
	private String cn_caller;       //序号
	private String cn_uascaller;       //序号
	
	public int getCn_id() {
		return cn_id;
	}
	public void setCn_id(int cn_id) {
		this.cn_id = cn_id;
	}
	public String getCn_title() {
		return cn_title;
	}
	public void setCn_title(String cn_title) {
		this.cn_title = cn_title;
	}
	public String getCn_url() {
		return cn_url;
	}
	public void setCn_url(String cn_url) {
		this.cn_url = cn_url;
	}
	public String getCn_icon() {
		return cn_icon;
	}
	public void setCn_icon(String cn_icon) {
		this.cn_icon = cn_icon;
	}
	public int getCn_detno() {
		return cn_detno;
	}
	public void setCn_detno(int cn_detno) {
		this.cn_detno = cn_detno;
	}
	public String getCn_caller() {
		return cn_caller;
	}
	public void setCn_caller(String cn_caller) {
		this.cn_caller = cn_caller;
	}
	public String getCn_uascaller() {
		return cn_uascaller;
	}
	public void setCn_uascaller(String cn_uascaller) {
		this.cn_uascaller = cn_uascaller;
	}
	public int getCn_subof() {
		return cn_subof;
	}
	public void setCn_subof(int cn_subof) {
		this.cn_subof = cn_subof;
	}
	public int getCn_isleaf() {
		return cn_isleaf;
	}
	public void setCn_isleaf(int cn_isleaf) {
		this.cn_isleaf = cn_isleaf;
	}
}
