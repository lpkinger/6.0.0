package com.uas.erp.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

public class TaskJSONTree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean allowDrag = false;
	private String cls = "";
	private String iconCls = "";
	private List<JSONTree> children;
	private boolean deleteable;
	private int tt_id;
	private String tt_name;
	private String tt_code;
	private int tt_ptid;
	private String tt_ptname;
	private int tt_parentid;
	private String tt_index;
	private String tt_startdate;
	private String tt_enddate;
	private String tt_isleaf;
	private boolean leaf = true;
	private SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");

	public boolean isAllowDrag() {
		return allowDrag;
	}

	public void setAllowDrag(boolean allowDrag) {
		this.allowDrag = allowDrag;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public List<JSONTree> getChildren() {
		return children;
	}

	public void setChildren(List<JSONTree> children) {
		this.children = children;
	}

	public boolean isDeleteable() {
		return deleteable;
	}

	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}

	public int getTt_id() {
		return tt_id;
	}

	public void setTt_id(int tt_id) {
		this.tt_id = tt_id;
	}

	public String getTt_name() {
		return tt_name;
	}

	public void setTt_name(String tt_name) {
		this.tt_name = tt_name;
	}

	public String getTt_code() {
		return tt_code;
	}

	public void setTt_code(String tt_code) {
		this.tt_code = tt_code;
	}

	public int getTt_ptid() {
		return tt_ptid;
	}

	public void setTt_ptid(int tt_ptid) {
		this.tt_ptid = tt_ptid;
	}

	public String getTt_ptname() {
		return tt_ptname;
	}

	public void setTt_ptname(String tt_ptname) {
		this.tt_ptname = tt_ptname;
	}

	public int getTt_parentid() {
		return tt_parentid;
	}

	public void setTt_parentid(int tt_parentid) {
		this.tt_parentid = tt_parentid;
	}

	public String getTt_index() {
		return tt_index;
	}

	public void setTt_index(String tt_index) {
		this.tt_index = tt_index;
	}

	public String getTt_startdate() {
		return tt_startdate;
	}

	public void setTt_startdate(String tt_startdate) {
		this.tt_startdate = tt_startdate;
	}

	public String getTt_enddate() {
		return tt_enddate;
	}

	public void setTt_enddate(String tt_enddate) {
		this.tt_enddate = tt_enddate;
	}

	public String getTt_isleaf() {
		return tt_isleaf;
	}

	public void setTt_isleaf(String tt_isleaf) {
		this.tt_isleaf = tt_isleaf;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public TaskJSONTree() {

	}

	/**
	 * 将systaskTemplate构造成treestore格式
	 */
	public TaskJSONTree(TaskTemplate tasktemplate) {
		this.tt_id = tasktemplate.getTt_id();
		this.tt_name = tasktemplate.getTt_name();
		this.deleteable = false;
		this.tt_code = tasktemplate.getTt_code();
		this.tt_ptid = tasktemplate.getTt_ptid();
		this.tt_ptname = tasktemplate.getTt_ptname();
		this.tt_index = tasktemplate.getTt_index();
		this.tt_parentid = tasktemplate.getTt_parentid();
		this.tt_startdate = simpledateformat.format(tasktemplate.getTt_startdate());
		this.tt_enddate = simpledateformat.format(tasktemplate.getTt_enddate());
		this.tt_isleaf = tasktemplate.getTt_isleaf();
		if (tasktemplate.getTt_isleaf().equals("F")) {
			this.allowDrag = false;
			this.leaf = false;
			if (tasktemplate.getTt_parentid() == 0) {
				// this.cls = "x-tree-cls-root";
			} else {
				// this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			// this.cls = "x-tree-cls-node";
		}
	}

}
