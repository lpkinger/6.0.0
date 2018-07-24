package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.dao.Saveable;

public class JProcess implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jp_id;
	private String jp_name;
	private String jp_launcherId;
	private String jp_launcherName;
	private String jp_form;
	private String jp_nodeId;
	private String jp_nodeName;
	private String jp_nodeDealMan;
	private String jp_nodeDealManName;
	private Date jp_launchTime;
	private long jp_stayMinutes;
	private String jp_caller;
	private String jp_table;
	private int jp_keyValue;
	private String jp_processInstanceId;
	private String jp_status;
	private String jp_keyName;
	private String jp_url;
	private String jp_formStatus;
	private int jp_flag;
	private String jp_formDetailKey;
	private String jp_codevalue;
	private int jp_pagingid;
	private String jp_processdefid;
	private String jp_processnote;
    private String jp_realjobid;
	public int getJp_id() {
		return jp_id;
	}

	public void setJp_id(int jp_id) {
		this.jp_id = jp_id;
	}

	public String getJp_name() {
		return jp_name;
	}

	public void setJp_name(String jp_name) {
		this.jp_name = jp_name;
	}

	public String getJp_launcherId() {
		return jp_launcherId;
	}

	public void setJp_launcherId(String jp_launcherId) {
		this.jp_launcherId = jp_launcherId;
	}

	public String getJp_launcherName() {
		return jp_launcherName;
	}

	public void setJp_launcherName(String jp_launcherName) {
		this.jp_launcherName = jp_launcherName;
	}

	public String getJp_form() {
		return jp_form;
	}

	public void setJp_form(String jp_form) {
		this.jp_form = jp_form;
	}

	public String getJp_nodeName() {
		return jp_nodeName;
	}

	public void setJp_nodeName(String jp_nodeName) {
		this.jp_nodeName = jp_nodeName;
	}

	public Date getJp_launchTime() {
		return jp_launchTime;
	}

	public void setJp_launchTime(Date jp_launchTime) {
		this.jp_launchTime = jp_launchTime;
	}

	public long getJp_stayMinutes() {
		return jp_stayMinutes;
	}

	public void setJp_stayMinutes(long jp_stayMinutes) {
		this.jp_stayMinutes = jp_stayMinutes;
	}

	public String getJp_caller() {
		return jp_caller;
	}

	public void setJp_caller(String jp_caller) {
		this.jp_caller = jp_caller;
	}

	public String getJp_table() {
		return jp_table;
	}

	public void setJp_table(String jp_table) {
		this.jp_table = jp_table;
	}

	public int getJp_keyValue() {
		return jp_keyValue;
	}

	public void setJp_keyValue(int jp_keyValue) {
		this.jp_keyValue = jp_keyValue;
	}

	public String getJp_processInstanceId() {
		return jp_processInstanceId;
	}

	public void setJp_processInstanceId(String jp_processInstanceId) {
		this.jp_processInstanceId = jp_processInstanceId;
	}

	public String getJp_nodeDealMan() {
		return jp_nodeDealMan;
	}

	public void setJp_nodeDealMan(String jp_nodeDealMan) {
		this.jp_nodeDealMan = jp_nodeDealMan;
	}
	
	public String getJp_nodeDealManName() {
		return jp_nodeDealManName;
	}

	public void setJp_nodeDealManName(String jp_nodeDealManName) {
		this.jp_nodeDealManName = jp_nodeDealManName;
	}
	
	public String getJp_nodeId() {
		return jp_nodeId;
	}

	public void setJp_nodeId(String jp_nodeId) {
		this.jp_nodeId = jp_nodeId;
	}

	public String getJp_status() {
		return jp_status;
	}

	public void setJp_status(String jp_status) {
		this.jp_status = jp_status;
	}

	public String getJp_keyName() {
		return jp_keyName;
	}

	public void setJp_keyName(String jp_keyName) {
		this.jp_keyName = jp_keyName;
	}

	public String getJp_url() {
		return jp_url;
	}

	public void setJp_url(String jp_url) {
		this.jp_url = jp_url;
	}

	public String getJp_formStatus() {
		return jp_formStatus;
	}

	public void setJp_formStatus(String jp_formStatus) {
		this.jp_formStatus = jp_formStatus;
	}

	public int getJp_flag() {
		return jp_flag;
	}

	public void setJp_flag(int jp_flag) {
		this.jp_flag = jp_flag;
	}

	public String getJp_formDetailKey() {
		return jp_formDetailKey;
	}

	public void setJp_formDetailKey(String jp_formDetailKey) {
		this.jp_formDetailKey = jp_formDetailKey;
	}

	public String getJp_codevalue() {
		return jp_codevalue;
	}

	public void setJp_codevalue(String jp_codevalue) {
		this.jp_codevalue = jp_codevalue;
	}

	public int getJp_pagingid() {
		return jp_pagingid;
	}

	public void setJp_pagingid(int jp_pagingid) {
		this.jp_pagingid = jp_pagingid;
	}

	public String getJp_processnote() {
		return jp_processnote;
	}

	public void setJp_processnote(String jp_processnote) {
		this.jp_processnote = jp_processnote;
	}

	public String getJp_processdefid() {
		return jp_processdefid;
	}

	public void setJp_processdefid(String jp_processdefid) {
		this.jp_processdefid = jp_processdefid;
	}  
   
	public String getJp_realjobid() {
		return jp_realjobid;
	}

	public void setJp_realjobid(String jp_realjobid) {
		this.jp_realjobid = jp_realjobid;
	}

	@Override
	public String table() {

		return "JProcess";
	}

	@Override
	public String[] keyColumns() {

		return new String[] { "jp_id" };
	}

}
