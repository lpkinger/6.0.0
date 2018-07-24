package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 初始化项目配置表
 */
public class Initialize implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int in_id;// ID
	private int in_pid;// 父节点ID
	private int in_leaf;// 是否叶节点
	private String in_img;// 关联图片
	private String in_caller;// 关联caller、表名
	private String in_desc;// 描述
	private String in_url;// 关联页面
	private String in_description;// 备注信息
	private String parentName;// 上级描述

	public int getIn_id() {
		return in_id;
	}

	public void setIn_id(int in_id) {
		this.in_id = in_id;
	}

	public int getIn_pid() {
		return in_pid;
	}

	public void setIn_pid(int in_pid) {
		this.in_pid = in_pid;
	}

	public int getIn_leaf() {
		return in_leaf;
	}

	public void setIn_leaf(int in_leaf) {
		this.in_leaf = in_leaf;
	}

	public String getIn_img() {
		return in_img;
	}

	public void setIn_img(String in_img) {
		this.in_img = in_img;
	}

	public String getIn_caller() {
		return in_caller;
	}

	public void setIn_caller(String in_caller) {
		this.in_caller = in_caller;
	}

	public String getIn_desc() {
		return in_desc;
	}

	public void setIn_desc(String in_desc) {
		this.in_desc = in_desc;
	}

	public String getIn_url() {
		return in_url;
	}

	public void setIn_url(String in_url) {
		this.in_url = in_url;
	}

	public String getIn_description() {
		return in_description;
	}

	public void setIn_description(String in_description) {
		this.in_description = in_description;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Override
	public String table() {
		return "Initialize";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "in_id" };
	}

}
