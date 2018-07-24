package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author yaozx
 */
public class CheckTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String text = "";
	private int parentId;
	private String url = "";
	private String qtitle = "";
	private boolean leaf = true;
	private boolean allowDrag = false;
	private String qtip = "";
	private String cls = "";
	private String iconCls = "";
	private List<CheckTree> children;
	private boolean using = false;
	private boolean checked = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQtitle() {
		return qtitle;
	}

	public void setQtitle(String qtitle) {
		this.qtitle = qtitle;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isAllowDrag() {
		return allowDrag;
	}

	public void setAllowDrag(boolean allowDrag) {
		this.allowDrag = allowDrag;
	}

	public String getQtip() {
		return qtip;
	}

	public void setQtip(String qtip) {
		this.qtip = qtip;
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

	public List<CheckTree> getChildren() {
		return children;
	}

	public void setChildren(List<CheckTree> children) {
		this.children = children;
	}

	public boolean isUsing() {
		return using;
	}

	public void setUsing(boolean using) {
		this.using = using;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public CheckTree(HROrg hrOrg, String language) {
		this.id = hrOrg.getOr_id();
		this.parentId = hrOrg.getOr_subof();
		this.text = hrOrg.getOr_headmanname() + "(" + hrOrg.getOr_department() + ")";
		this.qtip = hrOrg.getOr_headmancode();
		if (hrOrg.getOr_isleaf() == 0) {
			this.leaf = false;
			this.allowDrag = false;
		} else {
			this.leaf = false;
			this.allowDrag = true;
		}
	}

	public CheckTree(Employee employee, String language) {
		this.id = employee.getEm_id();
		this.parentId = employee.getEm_defaultorid();
		this.text = employee.getEm_name();
		this.qtip = employee.getEm_name();
		this.leaf = true;
		this.allowDrag = true;
	}

	public CheckTree() {

	}

}
