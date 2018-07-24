package com.uas.erp.model;

import java.io.Serializable;

/**
 * 权限表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class PurView implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pv_id;// ID
	private int pv_moduleid;// 模块ID
	private String pv_modulename;// 模块名称
	private int pv_formid;// FormID
	private String pv_formname;// Form名称
	private int pv_commandid;// 动作ID
	private String pv_commandname;// 动作名称
	private int pv_enid;// 企业ID

	public int getPv_id() {
		return pv_id;
	}

	public void setPv_id(int pv_id) {
		this.pv_id = pv_id;
	}

	public int getPv_moduleid() {
		return pv_moduleid;
	}

	public void setPv_moduleid(int pv_moduleid) {
		this.pv_moduleid = pv_moduleid;
	}

	public String getPv_modulename() {
		return pv_modulename;
	}

	public void setPv_modulename(String pv_modulename) {
		this.pv_modulename = pv_modulename;
	}

	public int getPv_formid() {
		return pv_formid;
	}

	public void setPv_formid(int pv_formid) {
		this.pv_formid = pv_formid;
	}

	public String getPv_formname() {
		return pv_formname;
	}

	public void setPv_formname(String pv_formname) {
		this.pv_formname = pv_formname;
	}

	public int getPv_commandid() {
		return pv_commandid;
	}

	public void setPv_commandid(int pv_commandid) {
		this.pv_commandid = pv_commandid;
	}

	public String getPv_commandname() {
		return pv_commandname;
	}

	public void setPv_commandname(String pv_commandname) {
		this.pv_commandname = pv_commandname;
	}

	public int getPv_enid() {
		return pv_enid;
	}

	public void setPv_enid(int pv_enid) {
		this.pv_enid = pv_enid;
	}
}
