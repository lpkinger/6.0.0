package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.core.support.SystemSession;

public class CheckBoxTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object id;
	private String text = "";
	private Object parentId;
	private String url = "";
	private String qtitle = "";
	private boolean leaf = true;
	private boolean allowDrag = false;
	private String qtip = "";
	private String cls = "";
	private String iconCls = "";
	private String currency = "";
	private String typename = "";
	private String caclass = "";
	private String caname = "";
	private String caasstype = "";
	private String caassname = "";
	private String calevel = "";
	private String code = "";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private Object data;

	public String getCalevel() {
		return calevel;
	}

	public void setCalevel(String calevel) {
		this.calevel = calevel;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	private int type;
	private List<CheckBoxTree> children;
	// private boolean deleteable;
	// private String updatetime;
	// private String version;
	// private String filesize;
	private boolean using = false;
	private Object checked;

	public String getCaasstype() {
		return caasstype;
	}

	public void setCaasstype(String caasstype) {
		this.caasstype = caasstype;
	}

	public String getCaassname() {
		return caassname;
	}

	public void setCaassname(String caassname) {
		this.caassname = caassname;
	}

	public String getCaname() {
		return caname;
	}

	public void setCaname(String caname) {
		this.caname = caname;
	}

	public String getCaclass() {
		return caclass;
	}

	public void setCaclass(String caclass) {
		this.caclass = caclass;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getChecked() {
		return checked;
	}

	public void setChecked(Object checked) {
		this.checked = checked;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Object getParentId() {
		return parentId;
	}

	public void setParentId(Object parentId) {
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

	public List<CheckBoxTree> getChildren() {
		return children;
	}

	public void setChildren(List<CheckBoxTree> children) {
		this.children = children;
	}

	public boolean isUsing() {
		return using;
	}

	public void setUsing(boolean using) {
		this.using = using;
	}

	public CheckBoxTree(HROrg hrOrg, String language) {
		this.id = String.valueOf(hrOrg.getOr_id());
		this.parentId = String.valueOf(hrOrg.getOr_subof());
		this.text = hrOrg.getOr_name();
		if (hrOrg.getOr_headmanname() != null) {
			this.text += "(" + hrOrg.getOr_headmanname() + ")";
		}
		this.cls="x-hrorgTree";
		this.qtip = hrOrg.getOr_headmancode();
		this.data = hrOrg;
		if (hrOrg.getOr_isleaf() == 0) {
			this.leaf = false;
			this.allowDrag = false;
		} else {
			this.leaf = false;
			this.allowDrag = true;
		}
	}

	public CheckBoxTree(HROrg hrOrg, String language, String caller) {
		this.id = String.valueOf(hrOrg.getOr_id());
		this.parentId = String.valueOf(hrOrg.getOr_subof());
		this.text = hrOrg.getOr_name() + "(" + hrOrg.getOr_headmanname() + ")";
		this.qtip = hrOrg.getOr_headmancode();
		this.data = hrOrg;
		if (caller != null && caller.equals("EmpWorkDateModelSet")) {
			this.checked = false;
		}

		if (hrOrg.getOr_isleaf() == 0) {
			this.leaf = false;
			this.allowDrag = false;
		} else {
			this.leaf = false;
			this.allowDrag = true;
		}
	}

	public CheckBoxTree(Employee employee, String language, boolean isOrgNode) {
		if (isOrgNode) {// 树节点ID需唯一，而em_id和or_id可能相同
			this.id = "emp-" + employee.getEm_id();
		} else {
			this.id = employee.getEm_id();
		}
		//存在一人多岗位 需要在岗位
		this.id=this.id+"-"+employee.getEm_imageid();
		this.parentId = String.valueOf(employee.getEm_defaultorid());
		this.text = employee.getEm_name();
		this.qtip = employee.getEm_name();
		this.leaf = true;
		this.allowDrag = true;
		this.cls="x-hrorgTree";
		if(isOrgNode){
			this.checked = false;
		}
		this.data = employee;
	}

	public CheckBoxTree(Employee employee, String language, boolean isOrgNode, String caller) {
		if (isOrgNode) {// 树节点ID需唯一，而em_id和or_id可能相同
			this.id = "emp-" + employee.getEm_id();
		} else {
			this.id = employee.getEm_id();
		}
		this.parentId = String.valueOf(employee.getEm_defaultorid());
		if (caller.equals("EmpWorkDateModelSet") && employee.getModel_name() != null) {
			this.text = employee.getEm_name() + "(" + employee.getModel_name() + ")";
		} else {
			this.text = employee.getEm_name();
		}
		this.code = employee.getEm_code();
		this.qtip = employee.getEm_name();
		this.leaf = true;
		this.allowDrag = true;
		this.checked = false;
		this.data = employee;
	}

	public CheckBoxTree() {

	}
	
	public CheckBoxTree(SysNavigation navigation) {
		String language = SystemSession.getLang();
		this.id = navigation.getSn_Id();
		this.parentId = navigation.getSn_ParentId();
		this.using = navigation.getSn_using() == 1;
		this.checked = false;
		if (language.equals("en_US")) {
			this.text = navigation.getSn_displayname_en();
			this.qtitle = navigation.getSn_displayname_en();
		} else if (language.equals("zh_TW")) {
			this.text = navigation.getSn_displayname_tw();
			this.qtitle = navigation.getSn_displayname_tw();
		} else {
			this.text = navigation.getSn_DisplayName();
			this.qtitle = navigation.getSn_DisplayName();
		}
		this.url = navigation.getSn_Url();
		if (navigation.getSn_isleaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (navigation.getSn_ParentId() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
			this.iconCls = navigation.getSn_icon();
		}
	}
}
