package com.uas.erp.model;

import java.io.Serializable;

/**
 * 流程相关处理人
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowMan implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fm_id;// ID
	private int fm_flowid;// 实例流程ID
	private int fm_boxid;// 实例节点ID
	private String fm_emid;// 员工账号
	private String fm_emname;// 员工名称
	private String fm_status;// 处理状态(no未触及,yes已触发,pass跳过,finish已处理)
	private String fm_isknow;// 是否已知
	private String fm_isdefault;// 是否默认责任人
	private int fm_level;// 层级

	public int getFm_id() {
		return fm_id;
	}

	public void setFm_id(int fm_id) {
		this.fm_id = fm_id;
	}

	public int getFm_flowid() {
		return fm_flowid;
	}

	public void setFm_flowid(int fm_flowid) {
		this.fm_flowid = fm_flowid;
	}

	public int getFm_boxid() {
		return fm_boxid;
	}

	public void setFm_boxid(int fm_boxid) {
		this.fm_boxid = fm_boxid;
	}

	public String getFm_emid() {
		return fm_emid;
	}

	public void setFm_emid(String fm_emid) {
		this.fm_emid = fm_emid;
	}

	public String getFm_emname() {
		return fm_emname;
	}

	public void setFm_emname(String fm_emname) {
		this.fm_emname = fm_emname;
	}

	public String getFm_status() {
		return fm_status;
	}

	public void setFm_status(String fm_status) {
		this.fm_status = fm_status;
	}

	public String getFm_isknow() {
		return fm_isknow;
	}

	public void setFm_isknow(String fm_isknow) {
		this.fm_isknow = fm_isknow;
	}

	public String getFm_isdefault() {
		return fm_isdefault;
	}

	public void setFm_isdefault(String fm_isdefault) {
		this.fm_isdefault = fm_isdefault;
	}

	public int getFm_level() {
		return fm_level;
	}

	public void setFm_level(int fm_level) {
		this.fm_level = fm_level;
	}
}
