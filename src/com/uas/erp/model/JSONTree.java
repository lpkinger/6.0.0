package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.excel.ExcelFileTemplate;

public class JSONTree implements Serializable {
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
	private int detno;
	private boolean allowDrag = false;
	private String qtip = "";
	private String cls = "";
	private String iconCls = "";
	private int showMode = 0;
	private List<JSONTree> children;
	private boolean deleteable;
	private String updatetime;
	private String version;
	private String creator;
	private int creator_id;
	private boolean using = false;
	private boolean expanded = false;
	private String caller;
	private Object data;
    private Object otherInfo;
    private String addurl;
	private int length;
	private String num;
	private Integer svnversion;
	private Integer updateflag;
	private String prefixcode;
	
	public String getPrefixcode() {
		return prefixcode;
	}

	public void setPrefixcode(String prefixcode) {
		this.prefixcode = prefixcode;
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

	public int getDetno() {
		return detno;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public void setDetno(int detno) {
		this.detno = detno;
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

	public int getShowMode() {
		return showMode;
	}

	public void setShowMode(int showMode) {
		this.showMode = showMode;
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isUsing() {
		return using;
	}

	public void setUsing(boolean using) {
		this.using = using;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(int creator_id) {
		this.creator_id = creator_id;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
    
	public Object getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(Object otherInfo) {
		this.otherInfo = otherInfo;
	} 
	public String getAddurl() {
		return addurl;
	}

	public void setAddurl(String addurl) {
		this.addurl = addurl;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public JSONTree() {
		super();
	}

	/**
	 * 将sysnavigation构造成treestore格式
	 */
	public JSONTree(SysNavigation navigation) {
		String language = SystemSession.getLang();
		this.id = navigation.getSn_Id();
		this.parentId = navigation.getSn_ParentId();
		this.deleteable = false;
		this.detno = navigation.getSn_detno();
		this.using = navigation.getSn_using() == 1;
		this.showMode = navigation.getSn_showmode();
		this.caller = navigation.getSn_caller();
		this.setNum(navigation.getSn_num());
		this.setSvnversion(navigation.getSn_svnversion());
		this.setUpdateflag(navigation.getSn_updateflag()==null?0:navigation.getSn_updateflag());
		if (navigation.getSn_deleteable() != null && navigation.getSn_deleteable().equals("T")) {
			this.deleteable = true;
		}
		if (language.equals("en_US")) {
			this.text = navigation.getSn_displayname_en();
			this.qtip = navigation.getSn_displayname_en();
		} else if (language.equals("zh_TW")) {
			this.text = navigation.getSn_displayname_tw();
			this.qtip = navigation.getSn_displayname_tw();
		} else {
			this.text = navigation.getSn_DisplayName();
			this.qtip = navigation.getSn_DisplayName();
		}
		this.url = navigation.getSn_Url();
		this.addurl=navigation.getSn_addurl();
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

	public JSONTree(SysNavigation navigation, boolean data) {
		String language = SystemSession.getLang();
		if (data) {
			this.data = navigation;
		}
		this.id = navigation.getSn_Id();
		this.parentId = navigation.getSn_ParentId();
		this.deleteable = false;
		this.detno = navigation.getSn_detno();
		this.using = navigation.getSn_using() == 1;
		this.showMode = navigation.getSn_showmode();
		this.caller = navigation.getSn_caller();
		if (navigation.getSn_deleteable() != null && navigation.getSn_deleteable().equals("T")) {
			this.deleteable = true;
		}
		if (language.equals("en_US")) {
			this.text = navigation.getSn_displayname_en();
			this.qtip = navigation.getSn_displayname_en();
		} else if (language.equals("zh_TW")) {
			this.text = navigation.getSn_displayname_tw();
			this.qtip = navigation.getSn_displayname_tw();
		} else {
			this.text = navigation.getSn_DisplayName();
			this.qtip = navigation.getSn_DisplayName();
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

	public JSONTree(DocumentCatalog documentCatalog) {
		String language = SystemSession.getLang();
		this.id = documentCatalog.getDc_Id();
		this.parentId = documentCatalog.getDc_ParentId();
		// this.deleteable = false;
		// if(documentCatalog.getDc_deleteable() != null &&
		// documentCatalog.getDc_deleteable().equals("T")){
		// this.deleteable = true;
		// }
		if (language.equals("en_US")) {
			this.text = documentCatalog.getDc_displayname_en();
			this.qtip = documentCatalog.getDc_displayname_en();
		} else if (language.equals("zh_TW")) {
			this.text = documentCatalog.getDc_displayname_tw();
			this.qtip = documentCatalog.getDc_displayname_tw();
		} else {
			this.text = documentCatalog.getDc_DisplayName();
			this.qtip = documentCatalog.getDc_DisplayName();
		}
		// this.url = documentCatalog.getDc_Url();
		// this.filesize = documentCatalog.getDc_filesize();
		this.creator = documentCatalog.getDc_creator();
		this.creator_id = documentCatalog.getDc_creator_id();
		this.version = documentCatalog.getDc_version();
		this.updatetime = documentCatalog.getDc_updatetime();
		// this.qtitle = navigation.getSn_TabTitle();
		if (documentCatalog.getDc_isfile().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (documentCatalog.getDc_ParentId() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	/**
	 * 将productkind拼成json格式的treepanel
	 */
	public JSONTree(ProductKind productKind) {
		String language = SystemSession.getLang();
		this.id = productKind.getPk_id();
		this.parentId = productKind.getPk_subof();
		this.data = productKind;
		if (language.equals("en_US")) {
			this.text = productKind.getPk_engname();
			this.qtip = productKind.getPk_code();
		} else {
			this.text = productKind.getPk_name();
			this.qtip = productKind.getPk_code();
		}
		if (productKind.getPk_leaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (productKind.getPk_subof() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	/**
	 * 将vendorKind拼成json格式的treepanel
	 */
	public JSONTree(VendorKind vendorKind) {
		this.id = vendorKind.getvk_id();
		this.parentId = vendorKind.getvk_subof();
		this.text = vendorKind.getvk_kind();
		this.qtip = vendorKind.getvk_code();
		if (vendorKind.getvk_leaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (vendorKind.getvk_subof() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	/**
	 * 将customerKind拼成json格式的treepanel
	 */
	public JSONTree(CustomerKind customerKind) {
		this.id = customerKind.getck_id();
		this.parentId = customerKind.getck_subof();
		this.text = customerKind.getck_kind();
		this.qtip = customerKind.getck_code();
		if (customerKind.getck_leaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (customerKind.getck_subof() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}
	/**
	 * 将ContractType拼成json格式的treepanel
	 */
	public JSONTree(ContractType contractType) {
		this.id = contractType.getCt_id();
		this.parentId = contractType.getCt_subof();
		this.text = contractType.getCt_name();
		this.qtip = contractType.getCt_code();
		this.length=contractType.getCt_length();
		if (contractType.getCt_leaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (contractType.getCt_subof() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	public JSONTree(HROrg hrOrg) {
		this.id = hrOrg.getOr_id();
		this.parentId = hrOrg.getOr_subof();
		this.text = hrOrg.getOr_name() + "(" + hrOrg.getOr_headmanname() + ")";
		this.qtip = hrOrg.getOr_code();
		this.leaf = false;
		this.cls = "x-tree-cls-parent";
	}
	public JSONTree(HROrg hrOrg,boolean noheader) {
		this.id = hrOrg.getOr_id();
		this.parentId = hrOrg.getOr_subof();
		this.text = hrOrg.getOr_name();
		this.qtip = hrOrg.getOr_code();
		this.leaf = false;
		this.data=hrOrg;
		this.cls = "x-tree-cls-parent";
	}
	public JSONTree(Category category) {
		this.id = category.getCa_id();
		this.parentId = category.getCa_subof();
		this.text = category.getCa_name();
		this.qtip = category.getCa_code();
		this.data = category;
		if (category.getCa_isleaf() == 0) {
			this.leaf = false;
			this.allowDrag = false;
			if (category.getCa_subof() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	public JSONTree(Category category, String masterName) {
		this.id = masterName + "_" + category.getCa_id();
		this.parentId = masterName + "_" + category.getCa_subof();
		this.text = category.getCa_name();
		this.qtip = category.getCa_code();
		category.setCURRENTMASTER(masterName);
		this.data = category;
		if (category.getCa_isleaf() == 0) {
			this.leaf = false;
			this.allowDrag = false;
			if (category.getCa_subof() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	public JSONTree(Master master) {
		this.id = "master_" + master.getMa_id();
		this.parentId = "master_" + master.getMa_pid();
		this.text = master.getMa_function();
		this.qtip = master.getMa_name();
		this.data = master;
		this.leaf = false;
		this.allowDrag = false;
		if (master.getMa_type() == 0) {
			this.cls = "x-tree-cls-root";
		} else {
			this.cls = "x-tree-cls-parent";
		}
	}

	public JSONTree(HROrg org, Object parentId, String prefix) {
		this.id = prefix + org.getOr_id();
		this.parentId = parentId;
		this.text = org.getOr_name();
		this.qtip = org.getOr_department() + "(" + org.getOr_headmanname() + ")";
		this.leaf = false;
		this.allowDrag = true;
		this.cls = "x-tree-cls-parent";
		this.data = org;
	}

	public JSONTree(HROrg org, int parentId) {
		this.id = org.getOr_id();
		this.parentId = parentId;
		this.text = org.getOr_name();
		this.qtip = org.getOr_department() + "(" + org.getOr_headmanname() + ")";
		this.leaf = false;
		this.allowDrag = true;
		this.data=org;
		this.cls = "x-tree-cls-parent";
	}

	/**
	 * 将employee构造成treestore格式
	 */
	public JSONTree(Object parentId) {
		Employee employee = SystemSession.getUser();
		// 按照部门组织分组
		this.id = -employee.getEm_id();
		this.parentId = parentId;
		this.deleteable = false;
		this.qtitle = StringUtil.nvl(employee.getEm_mobile(), "");
		this.text = employee.getEm_name();
		this.qtip = employee.getEm_email();
		this.leaf = true;
		this.allowDrag = true;
		this.cls = "x-tree-cls-node";
	}
	
	/**
	 * 将employee构造成treestore格式
	 */
	public JSONTree(Employee employee, Object parentId) {
		// 按照部门组织分组
		this.id = -employee.getEm_id();
		this.parentId = parentId;
		this.deleteable = false;
		this.qtitle = StringUtil.nvl(employee.getEm_mobile(), "");
		this.text = employee.getEm_name();
		this.qtip = employee.getEm_email();
		this.leaf = true;
		this.allowDrag = true;
		this.cls = "x-tree-cls-node";
	}

	/**
	 * team tree
	 * */
	public JSONTree(Team team) {
		this.id = team.getTeam_id();
		this.parentId = team.getTeam_prjid();
		this.deleteable = false;
		this.text = team.getTeam_name();
		this.qtip = team.getTeam_name();
		this.leaf = false;
		this.allowDrag = true;
		this.cls = "x-tree-cls-parent";
	}

	

	/**
	 * knowledgeTree
	 * */
	public JSONTree(KnowledgeModule module) {
		this.id = module.getKm_id();
		this.parentId = 0;
		this.deleteable = false;
		this.text = module.getKm_name();
		this.qtip = module.getKm_name();
		this.leaf = false;
		this.allowDrag = true;
		this.expanded = true;
		this.cls = "x-tree-cls-parent";
	}

	public JSONTree(KnowledgeKind kind) {
		this.id = kind.getKk_id();
		this.parentId = kind.getKk_kmid();
		this.deleteable = false;
		this.text = kind.getKk_kind();
		this.qtip = kind.getKk_kind();
		this.leaf = true;
		this.allowDrag = true;
		this.expanded = true;
		this.cls = "x-tree-cls-node";
	}

	public JSONTree(BorrowListModule module) {
		this.id = module.getBl_borrowercode();
		this.parentId = 0;
		this.deleteable = false;
		this.text = module.getBl_borrower();
		this.qtip = module.getBl_borrower();
		this.qtitle = module.getBl_borrowercode();
		this.leaf = true;
		this.allowDrag = true;
		this.expanded = true;
		this.cls = "x-tree-cls-node";
	}

	/**
	 * AddressBookGroup tree
	 * */
	public JSONTree(AddressBookGroup group) {
		this.id = group.getAg_id();
		this.parentId = 0;
		this.text = group.getAg_name();
		this.qtip = group.getAg_name();
		this.qtitle = group.getAg_remark();
		this.leaf = true;
		this.allowDrag = true;

	}

	public JSONTree(ProjectPlan plan) {
		this.id = plan.getPrjplan_id();
		this.text = plan.getPrjplan_prjname();
		this.qtip = plan.getPrjplan_prjname();
		this.leaf = true;
		this.allowDrag = true;
		this.expanded = true;
		this.cls = "x-tree-cls-node";
	}

	public JSONTree(ProjectPlan plan, String str) {
		this.id = plan.getPrjplan_id();
		this.text = plan.getPrjplan_prjname();
		this.qtip = plan.getPrjplan_prjname();
		this.leaf = false;
		this.allowDrag = true;
		this.expanded = false;
		this.cls = "x-tree-cls-node";
	}

	public JSONTree(WorkRecord record, String str) {
		this.id = record.getWr_id();
		this.text = record.getWr_recorder();
		this.leaf = str.equals("leaf");
		this.allowDrag = true;
		this.expanded = true;
		this.cls = "x-tree-cls-node";
	}

	/**
	 * 将addrBook拼成json格式的treepanel
	 */
	public JSONTree(EmployeeMail employeeMail) {
		String language = SystemSession.getLang();
		this.id = employeeMail.getEmm_id();
		this.parentId = employeeMail.getEmm_parentid();
		if (language.equals("en_US")) {
			this.text = employeeMail.getEmm_friendname();
		} else {
			this.text = employeeMail.getEmm_friendname();
		}
		if (employeeMail.getEmm_leaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (employeeMail.getEmm_parentid() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
		}
	}

	public JSONTree(JProcessDeploy jProcessDeploy) {
		this.id = jProcessDeploy.getJd_selfId();
		this.parentId = jProcessDeploy.getJd_parentId();
		this.text = jProcessDeploy.getJd_classifiedName();
		this.qtip = jProcessDeploy.getJd_classifiedName();
		this.url = jProcessDeploy.getJd_formUrl();
		this.using = true;
		// 暂时借用 类型为 String类型的字段 :
		this.creator = jProcessDeploy.getJd_caller();
		this.qtitle = jProcessDeploy.getJd_processDefinitionId();
		this.version = jProcessDeploy.getJd_processDefinitionName();
		if (jProcessDeploy.getJd_isLeaf() == 0) {
			this.leaf = false;
			this.allowDrag = false;
			this.using = true;
			if (jProcessDeploy.getJd_parentId() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}

		} else {

			this.leaf = true;
			this.allowDrag = true;
			this.cls = "x-tree-cls-node";
			this.text = jProcessDeploy.getJd_processDefinitionName();
			this.using = jProcessDeploy.getJd_enabled().equals("是") ? true : false;
		}
	}
	
	public JSONTree(CurNavigationTree curNavigationTree) {
		this.data = curNavigationTree;
		this.id = curNavigationTree.getCn_id();
		this.parentId = curNavigationTree.getCn_subof();
		this.text = curNavigationTree.getCn_title();
		this.detno = curNavigationTree.getCn_detno();
		if (curNavigationTree.getCn_isleaf()==0) {
			this.leaf = false;
		} else {
			this.leaf = true;
		}
		if (curNavigationTree.getCn_subof()==0) {
			this.cls = "x-tree-cls-root";
		} else {
			this.cls = "x-tree-cls-parent";
		}
	}

	public JSONTree(DocumentRoom documentroom) {
		this.id = documentroom.getDr_id();
		this.parentId = 0;
		this.deleteable = false;
		this.text = documentroom.getDr_name();
		this.qtip = documentroom.getDr_name();
		this.leaf = true;
		this.allowDrag = true;
		this.expanded = true;
		this.cls = "x-tree-cls-node";
	}

	public JSONTree(ProductType type) {
		this.id = type.getPt_id();
		this.parentId = 0;
		this.text = type.getPt_name();
		this.qtip = type.getPt_description();
		this.leaf = true;
		this.allowDrag = true;
	}
	public JSONTree(Teammember tm) {
		this.id = tm.getTm_id();
		this.parentId =0;
		this.deleteable = false;
		this.text = tm.getTm_employeename();
		this.qtip = tm.getTm_employeename();
		this.qtitle = tm.getTm_employeecode();
		this.leaf = true;
		this.allowDrag = true;
		this.cls = "x-tree-cls-node";
	}
	/**
	 * DOCUMENT
	 * */
	public JSONTree(Document doc) {
		this.id = doc.getDo_id();
		this.parentId = doc.getDo_parentid();
		this.text = doc.getDo_name();
		this.leaf = false;
		this.allowDrag = true;
	}

	public JSONTree(DocumentList dl) {
		this.id = dl.getDl_id();
		this.parentId = dl.getDl_parentid();
		this.text = dl.getDl_name();
		this.leaf = false;
		this.allowDrag = true;
		this.url=dl.getDl_virtualpath();
		this.prefixcode = dl.getDl_prefixcode();
	}
	
	/**
	 * 
	 * @param ExcelFileTemplate
	 */
	
	public JSONTree(ExcelFileTemplate et) {
		this.id = et.getFileid_tpl();
		this.parentId = et.getFilesubof_tpl();
		this.qtip =et.getFiledesc_tpl();
		this.text = et.getFilename_tpl();
		this.leaf = !et.getFilecategory_tpl();
		this.caller=et.getFilecaller_tpl();
		if (et.getFilecategory_tpl()) {
			this.cls = "x-tree-cls-parent";
		} else {
			this.cls = "x-tree-cls-root";
		}
		this.creator = et.getFileman_tpl();
/*		if (employeeMail.getEmm_parentid() == 0) {
			this.cls = "x-tree-cls-root";
		} else {
			this.cls = "x-tree-cls-parent";
		}*/
	}
	
	public JSONTree(FeedbackModule fm) {
		this.id = fm.getFm_id();
		this.parentId = fm.getFm_subof();
		this.qtip =fm.getFm_name();
		this.text = fm.getFm_name();
		this.leaf = !fm.getFm_isleaf();
	}
	
	public JSONTree(Bench bench) {
		this.id = bench.getBc_code();
		this.parentId = "bench";
		this.deleteable = false;
		this.text = bench.getBc_title();
		this.qtip =bench.getBc_title();
		this.leaf = true;
		this.allowDrag = true;
		this.expanded = true;
		this.using = bench.getBc_used()==1;
		this.detno = bench.getBc_detno();
		this.data = bench;
		this.iconCls = bench.getBc_icon()==null?"x-tree-icon-leaf":bench.getBc_icon();
		this.url = "jsps/common/bench/bench.jsp?bench="+bench.getBc_code()+(bench.getBc_urlcond()==null?"":"&"+bench.getBc_urlcond());
		//this.caller = bench.getBc_code();
		this.cls = "x-tree-cls-node";
	}
	
	
	
	
	
	public JSONTree(FlowDefine flowDefine) {
		this.data = flowDefine;
		this.id = flowDefine.getFd_id();
		this.parentId = flowDefine.getFd_parentid();
		this.deleteable = false;
		this.text = flowDefine.getFd_remark();
		this.allowDrag = true;
		this.detno = flowDefine.getFd_detno();
		this.text = flowDefine.getFd_name();
		this.url = "jsps/common/jprocessDeploy.jsp?formCondition=jd_idIS31739&gridCondition=nullIS31739&datalistId=13170";
		this.caller = flowDefine.getFd_caller();
		this.leaf = false;
		this.allowDrag = false;
		if (flowDefine.getFd_isleaf().equals("F")) {
			this.leaf = false;
			this.allowDrag = false;
			if (flowDefine.getFd_parentid() == 0) {
				this.cls = "x-tree-cls-root";
			} else {
				this.cls = "x-tree-cls-parent";
			}
		} else {
			if(flowDefine.getFd_status().equals("close")){
				this.cls = "x-tree-cls-close";
			}else if(flowDefine.getFd_status().equals("enable")){
				this.cls = "x-tree-cls-enable";
			}
			this.leaf = true;
			this.allowDrag = true;
		}
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Integer getSvnversion() {
		return svnversion;
	}

	public void setSvnversion(Integer svnversion) {
		this.svnversion = svnversion;
	}

	public Integer getUpdateflag() {
		return updateflag;
	}

	public void setUpdateflag(Integer updateflag) {
		this.updateflag = updateflag;
	}
}
