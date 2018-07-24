package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的navigation
 * 
 * @author yingp
 * 
 */
public class Navigation implements Saveable{

	private Integer sn_id;
	private String sn_displayname;
	private Integer sn_parentid;
	private String sn_url;
	private String sn_useremail;
	private String sn_tabtitle;
	private String sn_isleaf;// T,F
	private String sn_displayname_en;// English
	private String sn_displayname_tw;// 繁體
	private String sn_deleteable;// 是否(T,F)允许删除
	private Short sn_using;// 是否(1/0)启用
	private Short sn_showmode;// 显示模式:{0-选项卡式;1-弹出框式;2-空白页;3-窗口模式}
	private Integer sn_detno;// 排序的顺序
	private String sn_icon;// iconCls
	private Short sn_logic;// 是否(1/0)允许扩展逻辑
	private String sn_caller;// Caller
	private Short sn_limit;// 0-没有权限控制,1-有权限控制
	private String sn_addurl;

	private String plan_id;
	
	/**
	 * 选中子节点的情况
	 * <ol>
	 * <li>-1不存在子节点 或 子节点全部设置为 nocheck = true</li>
	 * <li>0 无子节点被勾选</li>
	 * <li>1 部分子节点被勾选</li>
	 * <li>2 全部子节点被勾选</li>
	 * </ol>
	 * 如果全部子节点被勾选，执行覆盖更新时，会先删除子节点
	 */
	private Short child_state;

	public String getSn_isleaf() {
		return sn_isleaf;
	}

	public Integer getSn_id() {
		return sn_id;
	}

	public void setSn_id(Integer sn_id) {
		this.sn_id = sn_id;
	}

	public String getSn_displayname() {
		return sn_displayname;
	}

	public void setSn_displayname(String sn_displayname) {
		this.sn_displayname = sn_displayname;
	}

	public Integer getSn_parentid() {
		return sn_parentid;
	}

	public void setSn_parentid(Integer sn_parentid) {
		this.sn_parentid = sn_parentid;
	}

	public String getSn_url() {
		return sn_url;
	}

	public void setSn_url(String sn_url) {
		this.sn_url = sn_url;
	}

	public String getSn_useremail() {
		return sn_useremail;
	}

	public void setSn_useremail(String sn_useremail) {
		this.sn_useremail = sn_useremail;
	}

	public String getSn_tabtitle() {
		return sn_tabtitle;
	}

	public void setSn_tabtitle(String sn_tabtitle) {
		this.sn_tabtitle = sn_tabtitle;
	}

	public void setSn_isleaf(String sn_isleaf) {
		this.sn_isleaf = sn_isleaf;
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

	public String getSn_deleteable() {
		return sn_deleteable;
	}

	public void setSn_deleteable(String sn_deleteable) {
		this.sn_deleteable = sn_deleteable;
	}

	public Short getSn_using() {
		return sn_using;
	}

	public void setSn_using(Short sn_using) {
		this.sn_using = sn_using;
	}

	public Short getSn_showmode() {
		return sn_showmode;
	}

	public void setSn_showmode(Short sn_showmode) {
		this.sn_showmode = sn_showmode;
	}

	public Integer getSn_detno() {
		return sn_detno;
	}

	public void setSn_detno(Integer sn_detno) {
		this.sn_detno = sn_detno;
	}

	public String getSn_icon() {
		return sn_icon;
	}

	public void setSn_icon(String sn_icon) {
		this.sn_icon = sn_icon;
	}

	public Short getSn_logic() {
		return sn_logic;
	}

	public void setSn_logic(Short sn_logic) {
		this.sn_logic = sn_logic;
	}

	public String getSn_caller() {
		return sn_caller;
	}

	public void setSn_caller(String sn_caller) {
		this.sn_caller = sn_caller;
	}

	public Short getSn_limit() {
		return sn_limit;
	}

	public void setSn_limit(Short sn_limit) {
		this.sn_limit = sn_limit;
	}

	public String getSn_addurl() {
		return sn_addurl;
	}

	public void setSn_addurl(String sn_addurl) {
		this.sn_addurl = sn_addurl;
	}

	public Short getChild_state() {
		return child_state;
	}

	public void setChild_state(Short child_state) {
		this.child_state = child_state;
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
		return "upgrade$navigation";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
