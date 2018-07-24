package com.uas.erp.model;

import java.io.Serializable;

import javax.mail.internet.InternetAddress;

/**
 * 导航栏
 */
public class SysNavigation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sn_Id;
	private String sn_DisplayName;
	private int sn_ParentId;
	private String sn_Url;
	private String sn_UserEmail;
	private String sn_TabTitle;
	private String sn_isleaf;// T,F
	private String sn_displayname_en;// English
	private String sn_displayname_tw;// 繁體
	private String sn_deleteable;// 是否(T,F)允许删除
	private int sn_using;// 是否(1/0)启用
	private int sn_showmode;// 显示模式:{0-选项卡式;1-弹出框式;2-空白页;3-窗口模式}
	private int sn_detno;// 排序的顺序
	private String sn_icon;// iconCls
	private int sn_logic;// 是否(1/0)允许扩展逻辑
	private String sn_caller;// Caller
	private int sn_limit;// 0-没有权限控制,1-有权限控制
	private String sn_addurl;
	private int sn_show;// 在全功能导航显示
	private String sn_standardDesc;//标准描述
	private String sn_num;
	private Integer sn_svnversion;
	private Integer sn_updateflag;

	public String toString() {
		return "id:" + sn_Id + "  parentId:" + sn_ParentId + "  text:" + sn_DisplayName;
	}

	public int getSn_Id() {
		return sn_Id;
	}

	public void setSn_Id(int sn_Id) {
		this.sn_Id = sn_Id;
	}

	public String getSn_TabTitle() {
		return sn_TabTitle;
	}

	public void setSn_TabTitle(String sn_TabTitle) {
		this.sn_TabTitle = sn_TabTitle;
	}

	public String getSn_DisplayName() {
		return sn_DisplayName;
	}

	public void setSn_DisplayName(String sn_DisplayName) {
		this.sn_DisplayName = sn_DisplayName;
	}

	public int getSn_ParentId() {
		return sn_ParentId;
	}

	public void setSn_ParentId(int sn_ParentId) {
		this.sn_ParentId = sn_ParentId;
	}

	public String getSn_icon() {
		return sn_icon;
	}

	public void setSn_icon(String sn_icon) {
		this.sn_icon = sn_icon;
	}

	public String getSn_Url() {
		return sn_Url;
	}

	public void setSn_Url(String sn_Url) {
		this.sn_Url = sn_Url;
	}

	public String getSn_UserEmail() {
		return sn_UserEmail;
	}

	public void setSn_UserEmail(String sn_UserEmail) {
		this.sn_UserEmail = sn_UserEmail;
	}

	public String getSn_isleaf() {
		return sn_isleaf;
	}

	public void setSn_isleaf(String sn_isleaf) {
		this.sn_isleaf = sn_isleaf;
	}

	public int getSn_showmode() {
		return sn_showmode;
	}

	public void setSn_showmode(int sn_showmode) {
		this.sn_showmode = sn_showmode;
	}

	public int getSn_logic() {
		return sn_logic;
	}

	public void setSn_logic(int sn_logic) {
		this.sn_logic = sn_logic;
	}

	public String getSn_caller() {
		return sn_caller;
	}

	public void setSn_caller(String sn_caller) {
		this.sn_caller = sn_caller;
	}

	public int getSn_detno() {
		return sn_detno;
	}

	public void setSn_detno(int sn_detno) {
		this.sn_detno = sn_detno;
	}

	public String getSn_displayname_en() {
		return sn_displayname_en;
	}

	public void setSn_displayname_en(String sn_displayname_en) {
		this.sn_displayname_en = sn_displayname_en;
	}

	public String getSn_displayname_tw() {
		return sn_displayname_tw;
	}

	public void setSn_displayname_tw(String sn_displayname_tw) {
		this.sn_displayname_tw = sn_displayname_tw;
	}

	public int getSn_using() {
		return sn_using;
	}

	public void setSn_using(int sn_using) {
		this.sn_using = sn_using;
	}

	public String getSn_deleteable() {
		return sn_deleteable;
	}

	public void setSn_deleteable(String sn_deleteable) {
		this.sn_deleteable = sn_deleteable;
	}

	public int getSn_limit() {
		return sn_limit;
	}

	public void setSn_limit(int sn_limit) {
		this.sn_limit = sn_limit;
	}

	public String getSn_addurl() {
		return sn_addurl;
	}

	public void setSn_addurl(String sn_addurl) {
		this.sn_addurl = sn_addurl;
	}

	@Override
	public int hashCode() {
		return this.sn_Id;
	}

	@Override
	public boolean equals(Object paramObject) {
		if (paramObject == null)
			return false;
		if (this == paramObject)
			return true;
		if (paramObject instanceof SysNavigation) {
			SysNavigation sn = (SysNavigation) paramObject;
			if (sn.getSn_Id() == this.sn_Id)
				return true;
		}
		return false;
	}

	public String getSn_standardDesc() {
		return sn_standardDesc;
	}

	public void setSn_standardDesc(String sn_standardDesc) {
		this.sn_standardDesc = sn_standardDesc;
	}

	public int getSn_show() {
		return sn_show;
	}

	public void setSn_show(int sn_show) {
		this.sn_show = sn_show;
	}

	public String getSn_num() {
		return sn_num;
	}

	public void setSn_num(String sn_num) {
		this.sn_num = sn_num;
	}

	public Integer getSn_svnversion() {
		return sn_svnversion;
	}

	public void setSn_svnversion(Integer sn_svnversion) {
		this.sn_svnversion = sn_svnversion;
	}

	public Integer getSn_updateflag() {
		return sn_updateflag;
	}

	public void setSn_updateflag(Integer sn_updateflag) {
		this.sn_updateflag = sn_updateflag;
	}

}
