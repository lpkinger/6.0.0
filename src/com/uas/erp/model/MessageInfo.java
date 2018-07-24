package com.uas.erp.model;

import java.io.Serializable;

/**
 * 消息
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class MessageInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mi_id;// ID
	private String mi_from;// 消息来源:B2B、审批流、系统内部
	private String mi_isknow;// 是否已知
	private String mi_formuurl;// 查看单据链接
	private int mi_emid;// 接收人id
	private String mi_title;// 消息抬头，显示给用户的
	private int mi_enid;// 企业ID

	public int getMi_id() {
		return mi_id;
	}

	public void setMi_id(int mi_id) {
		this.mi_id = mi_id;
	}

	public String getMi_from() {
		return mi_from;
	}

	public void setMi_from(String mi_from) {
		this.mi_from = mi_from;
	}

	public String getMi_isknow() {
		return mi_isknow;
	}

	public void setMi_isknow(String mi_isknow) {
		this.mi_isknow = mi_isknow;
	}

	public String getMi_formuurl() {
		return mi_formuurl;
	}

	public void setMi_formuurl(String mi_formuurl) {
		this.mi_formuurl = mi_formuurl;
	}

	public int getMi_emid() {
		return mi_emid;
	}

	public void setMi_emid(int mi_emid) {
		this.mi_emid = mi_emid;
	}

	public String getMi_title() {
		return mi_title;
	}

	public void setMi_title(String mi_title) {
		this.mi_title = mi_title;
	}

	public int getMi_enid() {
		return mi_enid;
	}

	public void setMi_enid(int mi_enid) {
		this.mi_enid = mi_enid;
	}
}
