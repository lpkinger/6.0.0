package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.dao.Saveable;

public class CurNavigation implements  Serializable,Saveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cn_id;
	private String cn_title;
	private String cn_url;
	private String cn_icon;
	private int cn_isleaf;
	private int cn_detno;
	private int cn_subof;
	private List<CurNavigation> children;
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

	public int getCn_isleaf() {
		return cn_isleaf;
	}

	public void setCn_isleaf(int cn_isleaf) {
		this.cn_isleaf = cn_isleaf;
	}

	public int getCn_detno() {
		return cn_detno;
	}

	public void setCn_detno(int cn_detno) {
		this.cn_detno = cn_detno;
	}   	
	public int getCn_subof() {
		return cn_subof;
	}
	public void setCn_subof(int cn_subof) {
		this.cn_subof = cn_subof;
	}

	public List<CurNavigation> getChildren() {
		return children;
	}

	public void setChildren(List<CurNavigation> children) {
		this.children = children;
	}

	@Override
	public String table() {
		// TODO Auto-generated method stub
		return "CurNavigation";
	}

	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return new String[]{"cn_id"};
	}

}
