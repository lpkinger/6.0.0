package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class Synergy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sy_id;
	private String sy_title;
	private String sy_process;
	private String sy_type;
	private String sy_content;
	private String sy_releaser;
	private int sy_releaser_id;
	private String sy_mold;
	private int sy_process_id;
	private String sy_status;
	private String sy_statuscode;
	private String sy_depart;
	private String sy_attach_id;
	private Date sy_date;

	public int getSy_id() {
		return sy_id;
	}

	public void setSy_id(int sy_id) {
		this.sy_id = sy_id;
	}

	public String getSy_title() {
		return sy_title;
	}

	public void setSy_title(String sy_title) {
		this.sy_title = sy_title;
	}

	public String getSy_process() {
		return sy_process;
	}

	public void setSy_process(String sy_process) {
		this.sy_process = sy_process;
	}

	public String getSy_type() {
		return sy_type;
	}

	public void setSy_type(String sy_type) {
		this.sy_type = sy_type;
	}

	public String getSy_content() {
		return sy_content;
	}

	public void setSy_content(String sy_content) {
		this.sy_content = sy_content;
	}

	public String getSy_releaser() {
		return sy_releaser;
	}

	public void setSy_releaser(String sy_releaser) {
		this.sy_releaser = sy_releaser;
	}

	public int getSy_releaser_id() {
		return sy_releaser_id;
	}

	public void setSy_releaser_id(int sy_releaser_id) {
		this.sy_releaser_id = sy_releaser_id;
	}

	public String getSy_mold() {
		return sy_mold;
	}

	public void setSy_mold(String sy_mold) {
		this.sy_mold = sy_mold;
	}

	public int getSy_process_id() {
		return sy_process_id;
	}

	public void setSy_process_id(int sy_process_id) {
		this.sy_process_id = sy_process_id;
	}

	public String getSy_status() {
		return sy_status;
	}

	public void setSy_status(String sy_status) {
		this.sy_status = sy_status;
	}

	public String getSy_statuscode() {
		return sy_statuscode;
	}

	public void setSy_statuscode(String sy_statuscode) {
		this.sy_statuscode = sy_statuscode;
	}

	public String getSy_depart() {
		return sy_depart;
	}

	public void setSy_depart(String sy_depart) {
		this.sy_depart = sy_depart;
	}

	public String getSy_attach_id() {
		return sy_attach_id;
	}

	public void setSy_attach_id(String sy_attach_id) {
		this.sy_attach_id = sy_attach_id;
	}

	public Date getSy_date() {
		return sy_date;
	}

	public void setSy_date(Date sy_date) {
		this.sy_date = sy_date;
	}

}
