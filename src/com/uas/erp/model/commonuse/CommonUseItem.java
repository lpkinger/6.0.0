package com.uas.erp.model.commonuse;

import java.io.Serializable;

/**
 * 常用功能项实体
 * @author zhuth
 * @time 2018年4月28日
 */
public class CommonUseItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int cu_id; // id
	private String cu_itemid; // 对应菜单节点id
	private String cu_parentid; // 父节点id
	private String cu_text; // 名称
	private int cu_group; // 是否是分组
	private String cu_groupid; // 所属分组id
	private int cu_index; // 序列号
	private int cu_expanded; // 是否展开
	private String cu_url; // 关联链接
	private String cu_addurl; // 关联新增链接 
	
	public int getCu_id() {
		return cu_id;
	}
	public void setCu_id(int cu_id) {
		this.cu_id = cu_id;
	}
	public String getCu_itemid() {
		return cu_itemid;
	}
	public void setCu_itemid(String cu_itemid) {
		this.cu_itemid = cu_itemid;
	}
	public String getCu_parentid() {
		return cu_parentid;
	}
	public void setCu_parentid(String cu_parentid) {
		this.cu_parentid = cu_parentid;
	}
	public String getCu_text() {
		return cu_text;
	}
	public void setCu_text(String cu_text) {
		this.cu_text = cu_text;
	}
	public int getCu_group() {
		return cu_group;
	}
	public void setCu_group(int cu_group) {
		this.cu_group = cu_group;
	}
	public String getCu_groupid() {
		return cu_groupid;
	}
	public void setCu_groupid(String cu_groupid) {
		this.cu_groupid = cu_groupid;
	}
	public int getCu_index() {
		return cu_index;
	}
	public void setCu_index(int cu_index) {
		this.cu_index = cu_index;
	}
	public int getCu_expanded() {
		return cu_expanded;
	}
	public void setCu_expanded(int cu_expanded) {
		this.cu_expanded = cu_expanded;
	}
	public String getCu_url() {
		return cu_url;
	}
	public void setCu_url(String cu_url) {
		this.cu_url = cu_url;
	}
	public String getCu_addurl() {
		return cu_addurl;
	}
	public void setCu_addurl(String cu_addurl) {
		this.cu_addurl = cu_addurl;
	}
}
