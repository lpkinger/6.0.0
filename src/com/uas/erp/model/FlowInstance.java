package com.uas.erp.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;

public class FlowInstance {
	
	public final static String TABLE="FLOW_INSTANCE";
	public final static String FI_ID="FI_ID";
	public final static String FI_FDSHORTNAME="FI_FDSHORTNAME";
	public final static String FI_NODEID="FI_NODEID";
	public final static String FI_CODEVALUE="FI_CODEVALUE";
	public final static String FI_KEYVALUE="FI_KEYVALUE";
	public final static String FI_HANDLER="FI_HANDLER";
	public final static String FI_HANDLERCODE="FI_HANDLERCODE";
	public final static String FI_TIME="FI_TIME";
	public final static String FI_NODENAME="FI_NODENAME";
	public final static String FI_STARTTIME="FI_STARTTIME";
	public final static String FI_STARTMAN="FI_STARTMAN";
	public final static String FI_STATUS="FI_STATUS";
	public final static String FI_STARTMANCODE="FI_STARTMANCODE";
	public final static String FI_CALLER="FI_CALLER";
	public final static String FI_KEYFIELD="FI_KEYFIELD";
	public final static String FI_TITLE="FI_TITLE";
	public final static String FI_TYPE="FI_TYPE";
	public final static String FI_FLID="FI_FLID";
	public final static String FIELD="FI_ID,FI_FDSHORTNAME,FI_NODEID,FI_CODEVALUE,FI_KEYVALUE,"
			+ "FI_HANDLER,FI_HANDLERCODE,FI_TIME,FI_NODENAME,FI_STARTTIME,FI_STARTMAN,FI_STATUS,"
			+ "FI_STARTMANCODE,FI_TITLE,FI_CALLER,FI_KEYFIELD,FI_TYPE,FI_FLID";
	private Integer fi_id;
	private String fi_fdshortname;
	private Integer fi_nodeid;
	private String fi_codevalue;
	private Integer fi_keyvalue;
	private String fi_handler;
	private String fi_handlercode;
	private String fi_time;
	private String fi_nodename;
	private String fi_starttime;
	private String fi_startman;
	private String fi_status;
	private String fi_startmancode;
	private String fi_caller;
	private String fi_keyfield;
	private String fi_title;
	private String fi_type;
	private Integer fi_flid;
	public Integer getFi_id() {
		return fi_id;
	}
	public void setFi_id(Integer fi_id) {
		this.fi_id = fi_id;
	}
	public String getFi_fdshortname() {
		return fi_fdshortname;
	}
	public void setFi_fdshortname(String fi_fdshortname) {
		this.fi_fdshortname = fi_fdshortname;
	}
	public Integer getFi_nodeid() {
		return fi_nodeid;
	}
	public void setFi_nodeid(Integer fi_nodeid) {
		this.fi_nodeid = fi_nodeid;
	}
	public String getFi_codevalue() {
		return fi_codevalue;
	}
	public void setFi_codevalue(String fi_codevalue) {
		this.fi_codevalue = fi_codevalue;
	}
	public Integer getFi_keyvalue() {
		return fi_keyvalue;
	}
	public void setFi_keyvalue(Integer fi_keyvalue) {
		this.fi_keyvalue = fi_keyvalue;
	}
	public String getFi_handler() {
		return fi_handler;
	}
	public void setFi_handler(String fi_handler) {
		this.fi_handler = fi_handler;
	}
	public String getFi_handlercode() {
		return fi_handlercode;
	}
	public void setFi_handlercode(String fi_handlercode) {
		this.fi_handlercode = fi_handlercode;
	}
	public String getFi_time() {
		return fi_time;
	}
	public void setFi_time(String fi_time) {
		this.fi_time = fi_time;
	}
	public String getFi_nodename() {
		return fi_nodename;
	}
	public void setFi_nodename(String fi_nodename) {
		this.fi_nodename = fi_nodename;
	}
	public String getFi_starttime() {
		return fi_starttime;
	}
	public void setFi_starttime(String fi_starttime) {
		this.fi_starttime = fi_starttime;
	}
	public String getFi_startman() {
		return fi_startman;
	}
	public void setFi_startman(String fi_startman) {
		this.fi_startman = fi_startman;
	}
	public String getFi_status() {
		return fi_status;
	}
	public void setFi_status(String fi_status) {
		this.fi_status = fi_status;
	}
	public String getFi_startmancode() {
		return fi_startmancode;
	}
	public void setFi_startmancode(String fi_startmancode) {
		this.fi_startmancode = fi_startmancode;
	}
	public String getFi_caller() {
		return fi_caller;
	}
	public void setFi_caller(String fi_caller) {
		this.fi_caller = fi_caller;
	}
	public String getFi_keyfield() {
		return fi_keyfield;
	}
	public void setFi_keyfield(String fi_keyfield) {
		this.fi_keyfield = fi_keyfield;
	}
	public String getFi_title() {
		return fi_title;
	}
	public void setFi_title(String fi_title) {
		this.fi_title = fi_title;
	}
	public String getFi_type() {
		return fi_type;
	}
	public void setFi_type(String fi_type) {
		this.fi_type = fi_type;
	}
	public Integer getFi_flid() {
		return fi_flid;
	}
	public void setFi_flid(Integer fi_flid) {
		this.fi_flid = fi_flid;
	}
	public String getValues(FlowInstance flowInstance) {
		StringBuffer sql = new StringBuffer();
		sql.append("'"+flowInstance.getFi_id()+"',");
		sql.append("'"+flowInstance.getFi_fdshortname()+"',");
		sql.append("'"+flowInstance.getFi_nodeid()+"',");
		sql.append("'"+flowInstance.getFi_codevalue()+"',");
		sql.append("'"+flowInstance.getFi_keyvalue()+"',");
		sql.append("'"+flowInstance.getFi_handler()+"',");
		sql.append("'"+flowInstance.getFi_handlercode()+"',");
		sql.append(getSqlDate(flowInstance.getFi_time())+",");
		sql.append("'"+flowInstance.getFi_nodename()+"',");
		sql.append(getSqlDate(flowInstance.getFi_starttime())+",");
		sql.append("'"+flowInstance.getFi_startman()+"',");
		sql.append("'"+flowInstance.getFi_status()+"',");
		sql.append("'"+flowInstance.getFi_startmancode()+"',");
		sql.append("'"+flowInstance.getFi_title()+"',");
		sql.append("'"+flowInstance.getFi_caller()+"',");
		sql.append("'"+flowInstance.getFi_keyfield()+"',");
		sql.append("'"+flowInstance.getFi_type()+"',");
		sql.append("'"+flowInstance.getFi_flid()+"'");
		
		return sql.toString();
	}
	private String getSqlDate(String time) {
		String nowTime = "to_date('"+time+"','yyyy-MM-dd HH24:MI:ss')";
		return nowTime;
	}
}
