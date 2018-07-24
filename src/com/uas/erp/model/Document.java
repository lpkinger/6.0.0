package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int do_id;
	private String do_code;
	private String do_status;
	private String do_statuscode;
	private int do_islocked;
	private String do_kind;
	private String do_name;
	private float do_size;
	private Date do_date;
	private String do_recorder;
	private int do_parentid;
	private String do_keywords;
	private String do_remark;

	public int getDo_id() {
		return do_id;
	}

	public void setDo_id(int do_id) {
		this.do_id = do_id;
	}

	public String getDo_code() {
		return do_code;
	}

	public void setDo_code(String do_code) {
		this.do_code = do_code;
	}

	public String getDo_status() {
		return do_status;
	}

	public void setDo_status(String do_status) {
		this.do_status = do_status;
	}

	public String getDo_statuscode() {
		return do_statuscode;
	}

	public void setDo_statuscode(String do_statuscode) {
		this.do_statuscode = do_statuscode;
	}

	public int getDo_islocked() {
		return do_islocked;
	}

	public void setDo_islocked(int do_islocked) {
		this.do_islocked = do_islocked;
	}

	public String getDo_kind() {
		return do_kind;
	}

	public void setDo_kind(String do_kind) {
		this.do_kind = do_kind;
	}

	public String getDo_name() {
		return do_name;
	}

	public void setDo_name(String do_name) {
		this.do_name = do_name;
	}

	public float getDo_size() {
		return do_size;
	}

	public void setDo_size(float do_size) {
		this.do_size = do_size;
	}

	public Date getDo_date() {
		return do_date;
	}

	public void setDo_date(Date do_date) {
		this.do_date = do_date;
	}

	public String getDo_recorder() {
		return do_recorder;
	}

	public void setDo_recorder(String do_recorder) {
		this.do_recorder = do_recorder;
	}

	public int getDo_parentid() {
		return do_parentid;
	}

	public void setDo_parentid(int do_parentid) {
		this.do_parentid = do_parentid;
	}

	public String getDo_keywords() {
		return do_keywords;
	}

	public void setDo_keywords(String do_keywords) {
		this.do_keywords = do_keywords;
	}

	public String getDo_remark() {
		return do_remark;
	}

	public void setDo_remark(String do_remark) {
		this.do_remark = do_remark;
	}

}
