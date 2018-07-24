package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class GroupTree implements Serializable{

	private Integer cg_id;
	private String text;
	private String cg_email;
	private String cg_group;
	private String cg_emid;
	private boolean leaf = false;
	private boolean checked = false;
	private List<GroupTree> children;
	private String cls = "x-hrorgTree";
	
	public Integer getCg_id() {
		return cg_id;
	}
	public void setCg_id(Integer cg_id) {
		this.cg_id = cg_id;
	}
	public String getCg_emid() {
		return cg_emid;
	}
	public void setCg_emid(String cg_emid) {
		this.cg_emid = cg_emid;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCg_email() {
		return cg_email;
	}
	public void setCg_email(String cg_email) {
		this.cg_email = cg_email;
	}
	public String getCg_group() {
		return cg_group;
	}
	public void setCg_group(String cg_group) {
		this.cg_group = cg_group;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public List<GroupTree> getChildren() {
		return children;
	}
	public void setChildren(List<GroupTree> children) {
		this.children = children;
	}
	public GroupTree() {
	}
	public GroupTree(String text,String cg_group) {
		this.text = cg_group;
		this.cg_group = cg_group;
	}
	public GroupTree(int cg_id, String text, String cg_email, String cg_group,String cg_emid) {
		this.cg_id = cg_id;
		this.text = text;
		this.cg_email = cg_email;
		this.cg_group = cg_group;
		this.cg_emid = cg_emid;
	}
	
}
