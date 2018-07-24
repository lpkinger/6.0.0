package com.uas.mobile.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.dao.Saveable;

/**
 * 待办事宜对象，对应allprocess_undo_view视图，多加一个url属性（视图上也相应添加了）
 * @author suntg
 * @date 2014年9月4日 11:20:00
 */
public class AllProcess implements Saveable, Serializable{

	private static final long serialVersionUID = 9154546832037377352L;
	private int id;//ID
	private String taskid;//节点编号
	private String status;//当前状态
	private String mainname;//流程名称
	private String taskname;//节点名称
	private String codevalue;//单据编号
	private String type;//流程类型
	private String typecode;//流程类型码
	private String dealpersoncode;//处理人 -> em_code
	private String recorderid;//发起人ID
	private String recorder;//发起人名称
	private Date datetime;//发起时间
	private String defid;//流程版本号
	private String caller;//流程callr
	private String link;//对应URL地址
	private String master;//所属的账套
	
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public int getId() {
		return id;
	}
	public void setId(int jp_id) {
		this.id = jp_id;
	}
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String jp_nodeid) {
		this.taskid = jp_nodeid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String jp_status) {
		this.status = jp_status;
	}
	public String getMainname() {
		return mainname;
	}
	public void setMainname(String jp_name) {
		this.mainname = jp_name;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String jp_nodename) {
		this.taskname = jp_nodename;
	}
	public String getCodevalue() {
		return codevalue;
	}
	public void setCodevalue(String jp_codevalue) {
		this.codevalue = jp_codevalue;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypecode() {
		return typecode;
	}
	public void setTypecode(String typecode) {
		this.typecode = typecode;
	}
	public String getDealpersoncode() {
		return dealpersoncode;
	}
	public void setDealpersoncode(String dealpersoncode) {
		this.dealpersoncode = dealpersoncode;
	}
	public String getRecorderid() {
		return recorderid;
	}
	public void setRecorderid(String jp_launcherid) {
		this.recorderid = jp_launcherid;
	}
	public String getRecorder() {
		return recorder;
	}
	public void setRecorder(String jp_launchername) {
		this.recorder = jp_launchername;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date jp_launchtime) {
		this.datetime = jp_launchtime;
	}
	public String getDefid() {
		return defid;
	}
	public void setDefid(String jp_processdefid) {
		this.defid = jp_processdefid;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String jp_caller) {
		this.caller = jp_caller;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String url) {
		this.link = url;
	}
	@Override
	public String table() {
		return "allprocess_undo_view";
	}
	@Override
	public String[] keyColumns() {
		return new String[]{"id"};
	}
	
	
}
