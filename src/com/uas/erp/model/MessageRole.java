package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;


/**
 * MessageModel
 */
public class MessageRole implements  Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mr_id;
	private int mr_iscombine;
	private int mr_ispopwin;
	private int mr_isused;
	private int mr_mmid;
	private String mr_combinecond;
	private String mr_desc;
	private String mr_level;
	private String mr_manids;
	private String mr_mans;
	private String mr_messagedemo;
	private String mr_messagestr;
	private String mr_sql;
	private String mr_wintype;
	public int getMr_id() {
		return mr_id;
	}
	public void setMr_id(int mr_id) {
		this.mr_id = mr_id;
	}
	public int getMr_iscombine() {
		return mr_iscombine;
	}
	public void setMr_iscombine(int mr_iscombine) {
		this.mr_iscombine = mr_iscombine;
	}
	public int getMr_ispopwin() {
		return mr_ispopwin;
	}
	public void setMr_ispopwin(int mr_ispopwin) {
		this.mr_ispopwin = mr_ispopwin;
	}
	public int getMr_isused() {
		return mr_isused;
	}
	public void setMr_isused(int mr_isused) {
		this.mr_isused = mr_isused;
	}
	public int getMr_mmid() {
		return mr_mmid;
	}
	public void setMr_mmid(int mr_mmid) {
		this.mr_mmid = mr_mmid;
	}
	public String getMr_combinecond() {
		return mr_combinecond;
	}
	public void setMr_combinecond(String mr_combinecond) {
		this.mr_combinecond = mr_combinecond;
	}
	public String getMr_desc() {
		return mr_desc;
	}
	public void setMr_desc(String mr_desc) {
		this.mr_desc = mr_desc;
	}
	public String getMr_level() {
		return mr_level;
	}
	public void setMr_level(String mr_level) {
		this.mr_level = mr_level;
	}
	public String getMr_manids() {
		return mr_manids;
	}
	public void setMr_manids(String mr_manids) {
		this.mr_manids = mr_manids;
	}
	public String getMr_mans() {
		return mr_mans;
	}
	public void setMr_mans(String mr_mans) {
		this.mr_mans = mr_mans;
	}
	public String getMr_messagedemo() {
		return mr_messagedemo;
	}
	public void setMr_messagedemo(String mr_messagedemo) {
		this.mr_messagedemo = mr_messagedemo;
	}
	public String getMr_messagestr() {
		return mr_messagestr;
	}
	public void setMr_messagestr(String mr_messagestr) {
		this.mr_messagestr = mr_messagestr;
	}
	public String getMr_sql() {
		return mr_sql;
	}
	public void setMr_sql(String mr_sql) {
		this.mr_sql = mr_sql;
	}
	public String getMr_wintype() {
		return mr_wintype;
	}
	public void setMr_wintype(String mr_wintype) {
		this.mr_wintype = mr_wintype;
	}
	
	
	
}