package com.uas.erp.model;

public class FlowOperation {
	
	public final static String TABLE="FLOW_OPERATION";
	public final static String FIELD="FO_ID,FO_NAME,FO_TYPE,FO_NEXTNODENAME,FO_NODENAME," + 
			"FO_REMARK,FO_GROUPNAME,FO_FLOWNAME,FO_FLOWNODENAME,FO_FDSHORTNAME,FO_FLOWCALLER,"
			+ "FO_FLOWNODEID,FO_URL,FO_ISDUTY,FO_NEXTNODEID,FO_NODEID,FO_CONDITION";
	public final static String FO_ID="FO_ID";
	public final static String FO_NAME="FO_NAME";
	public final static String FO_TYPE="FO_TYPE";
	public final static String FO_NEXTNODENAME="FO_NEXTNODENAME";
	public final static String FO_REMARK="FO_REMARK";
	public final static String FO_NODENAME="FO_NODENAME";
	public final static String FO_GROUPNAME="FO_GROUPNAME";
	public final static String FO_FLOWNAME="FO_FLOWNAME";
	public final static String FO_FLOWNODENAME="FO_FLOWNODENAME";
	public final static String FO_FDSHORTNAME="FO_FDSHORTNAME";
	public final static String FO_FLOWCALLER="FO_FLOWCALLER";
	public final static String FO_FLOWNODEID="FO_FLOWNODEID";
	public final static String FO_URL="FO_URL";
	public final static String FO_ISDUTY="FO_ISDUTY";
	public final static String FO_NEXTNODEID="FO_NEXTNODEID";
	public final static String FO_NODEID="FO_NODEID";
	public final static String FO_CONDITION="FO_CONDITION";
	
	private Integer fo_id;				//ID
	private String fo_name;				//操作名称(按钮名称)
	private String fo_type;				//类型
	private String fo_nextNodename;		//下一节点名称
	private String fo_remark;			//描述
	private String fo_nodeName;			//当前节点名称
	private String fo_groupName;		//页面分组名称
	private String fo_flowName;			//派生流程实例名称，对应派生流程版本简称
	private String fo_flowNodename;		//派生流程节点
	private String fo_fdshortname;      //版本名称
	private String fo_fdShortName;		//版本简称
	private String fo_flowcaller;       //流程caller
	private Integer fo_flownodeid;       //派生流程节点id
	private String fo_url;				//派生地址
	private String fo_isduty;           //是否被责任人修改
	private String fo_nextnodeid;       //下一节点id
	private String fo_nodeid;           //当前节点id
	private String fo_condition;        //决策条件
	public Integer getFo_id() {
		return fo_id;
	}
	public void setFo_id(Integer fo_id) {
		this.fo_id = fo_id;
	}
	public String getFo_name() {
		return fo_name;
	}
	public void setFo_name(String fo_name) {
		this.fo_name = fo_name;
	}
	public String getFo_type() {
		return fo_type;
	}
	public void setFo_type(String fo_type) {
		this.fo_type = fo_type;
	}
	public String getFo_nextNodename() {
		return fo_nextNodename;
	}
	public void setFo_nextNodename(String fo_nextNodename) {
		this.fo_nextNodename = fo_nextNodename;
	}
	public String getFo_remark() {
		return fo_remark;
	}
	public void setFo_remark(String fo_remark) {
		this.fo_remark = fo_remark;
	}
	public String getFo_nodeName() {
		return fo_nodeName;
	}
	public void setFo_nodeName(String fo_nodeName) {
		this.fo_nodeName = fo_nodeName;
	}
	public String getFo_groupName() {
		return fo_groupName;
	}
	public void setFo_groupName(String fo_groupName) {
		this.fo_groupName = fo_groupName;
	}
	public String getFo_flowName() {
		return fo_flowName;
	}
	public void setFo_flowName(String fo_flowName) {
		this.fo_flowName = fo_flowName;
	}
	public String getFo_flowNodename() {
		return fo_flowNodename;
	}
	public void setFo_flowNodename(String fo_flowNodename) {
		this.fo_flowNodename = fo_flowNodename;
	}
	public String getFo_fdshortname() {
		return fo_fdshortname;
	}
	public void setFo_fdshortname(String fo_fdshortname) {
		this.fo_fdshortname = fo_fdshortname;
	}
	public String getFo_fdShortName() {
		return fo_fdShortName;
	}
	public void setFo_fdShortName(String fo_fdShortName) {
		this.fo_fdShortName = fo_fdShortName;
	}
	public String getFo_flowcaller() {
		return fo_flowcaller;
	}
	public void setFo_flowcaller(String fo_flowcaller) {
		this.fo_flowcaller = fo_flowcaller;
	}
	public Integer getFo_flownodeid() {
		return fo_flownodeid;
	}
	public void setFo_flownodeid(Integer fo_flownodeid) {
		this.fo_flownodeid = fo_flownodeid;
	}
	public String getFo_url() {
		return fo_url;
	}
	public void setFo_url(String fo_url) {
		this.fo_url = fo_url;
	}
	public String getFo_isduty() {
		return fo_isduty;
	}
	public void setFo_isduty(String fo_isduty) {
		this.fo_isduty = fo_isduty;
	}
	public String getFo_nextnodeid() {
		return fo_nextnodeid;
	}
	public void setFo_nextnodeid(String fo_nextnodeid) {
		this.fo_nextnodeid = fo_nextnodeid;
	}
	public String getFo_nodeid() {
		return fo_nodeid;
	}
	public void setFo_nodeid(String fo_nodeid) {
		this.fo_nodeid = fo_nodeid;
	}
	public String getFo_condition() {
		return fo_condition;
	}
	public void setFo_condition(String fo_condition) {
		this.fo_condition = fo_condition;
	}
	
	public String getValues(FlowOperation flowOperation) {
		StringBuffer sql = new StringBuffer();
		sql.append("'"+flowOperation.getFo_id()+"'");
		sql.append("'"+flowOperation.getFo_name()+"'");
		sql.append("'"+flowOperation.getFo_type()+"'");
		sql.append("'"+flowOperation.getFo_nextNodename()+"'");
		sql.append("'"+flowOperation.getFo_nodeName()+"'");
		sql.append("'"+flowOperation.getFo_remark()+"'");
		sql.append("'"+flowOperation.getFo_groupName()+"'");
		sql.append("'"+flowOperation.getFo_flowName()+"'");
		sql.append("'"+flowOperation.getFo_flowNodename()+"'");
		sql.append("'"+flowOperation.getFo_fdShortName()+"'");
		sql.append("'"+flowOperation.getFo_flowcaller()+"'");
		sql.append("'"+flowOperation.getFo_flownodeid()+"'");
		sql.append("'"+flowOperation.getFo_url()+"'");
		sql.append("'"+flowOperation.getFo_isduty()+"'");
		sql.append("'"+flowOperation.getFo_nextnodeid()+"'");
		sql.append("'"+flowOperation.getFo_nodeid()+"'");
		sql.append("'"+flowOperation.getFo_condition()+"'");
		
		return sql.toString();
	}
}
