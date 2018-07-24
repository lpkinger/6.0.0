package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class WorkPlan implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int wp_id;
	private String wp_title;
	private String wp_type;
	private int wp_typeid;
	private String wp_emp;
	private int wp_empid;
	private String wp_time;
	private String wp_summary;
	private String wp_sumattachs;
	private String wp_status;
	private String wp_statuscode;
	private String wp_planattachs;
	private Date wp_committime;
	private Date wp_updatetime;
	public int getWp_id() {
		return wp_id;
	}
	public void setWp_id(int wp_id) {
		this.wp_id = wp_id;
	}
	public String getWp_title() {
		return wp_title;
	}
	public void setWp_title(String wp_title) {
		this.wp_title = wp_title;
	}
	public String getWp_type() {
		return wp_type;
	}
	public void setWp_type(String wp_type) {
		this.wp_type = wp_type;
	}
	public int getWp_typeid() {
		return wp_typeid;
	}
	public void setWp_typeid(int wp_typeid) {
		this.wp_typeid = wp_typeid;
	}
	public String getWp_emp() {
		return wp_emp;
	}
	public void setWp_emp(String wp_emp) {
		this.wp_emp = wp_emp;
	}
	public int getWp_empid() {
		return wp_empid;
	}
	public void setWp_empid(int wp_empid) {
		this.wp_empid = wp_empid;
	}
	public String getWp_time() {
		return wp_time;
	}
	public void setWp_time(String wp_time) {
		this.wp_time = wp_time;
	}
	public String getWp_summary() {
		return wp_summary;
	}
	public void setWp_summary(String wp_summary) {
		this.wp_summary = wp_summary;
	}
	public String getWp_sumattachs() {
		return wp_sumattachs;
	}
	public void setWp_sumattachs(String wp_sumattachs) {
		this.wp_sumattachs = wp_sumattachs;
	}
	public String getWp_status() {
		return wp_status;
	}
	public void setWp_status(String wp_status) {
		this.wp_status = wp_status;
	}
	public String getWp_statuscode() {
		return wp_statuscode;
	}
	public void setWp_statuscode(String wp_statuscode) {
		this.wp_statuscode = wp_statuscode;
	}
	public String getWp_planattachs() {
		return wp_planattachs;
	}
	public void setWp_planattachs(String wp_planattachs) {
		this.wp_planattachs = wp_planattachs;
	}
	public Date getWp_committime() {
		return wp_committime;
	}
	public void setWp_committime(Date wp_committime) {
		this.wp_committime = wp_committime;
	}
	public Date getWp_updatetime() {
		return wp_updatetime;
	}
	public void setWp_updatetime(Date wp_updatetime) {
		this.wp_updatetime = wp_updatetime;
	}
	
	

}
