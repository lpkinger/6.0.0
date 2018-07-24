package com.uas.erp.model;

import java.io.Serializable;

//文件上传临时记录表
public class FileUploadTemp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int fl_id;
	
	private String fl_name;
	
	private String fl_matchres;
	
	private String fl_matchstatus;
	
	private String fl_matchcode;
	
	private String fl_uploadres;
	
	private String fl_uploadstatus;
	
	private String fl_deptno;
	
	public FileUploadTemp() {
	}

	public int getFl_id() {
		return fl_id;
	}

	public void setFl_id(int fl_id) {
		this.fl_id = fl_id;
	}

	public String getFl_name() {
		return fl_name;
	}

	public void setFl_name(String fl_name) {
		this.fl_name = fl_name;
	}

	public String getFl_matchres() {
		return fl_matchres;
	}

	public void setFl_matchres(String fl_matchres) {
		this.fl_matchres = fl_matchres;
	}

	public String getFl_matchstatus() {
		return fl_matchstatus;
	}

	public void setFl_matchstatus(String fl_matchstatus) {
		this.fl_matchstatus = fl_matchstatus;
	}

	public String getFl_uploadres() {
		return fl_uploadres;
	}

	public void setFl_uploadres(String fl_uploadres) {
		this.fl_uploadres = fl_uploadres;
	}

	public String getFl_uploadstatus() {
		return fl_uploadstatus;
	}

	public void setFl_uploadstatus(String fl_uploadstatus) {
		this.fl_uploadstatus = fl_uploadstatus;
	}

	public String getFl_deptno() {
		return fl_deptno;
	}

	public void setFl_deptno(String fl_deptno) {
		this.fl_deptno = fl_deptno;
	}

	@Override
	public String toString() {
		return "FileUploadTemp [fl_id=" + fl_id + ", fl_name=" + fl_name + ", fl_matchres=" + fl_matchres
				+ ", fl_matchstatus=" + fl_matchstatus + ", fl_uploadres=" + fl_uploadres + ", fl_uploadstatus="
				+ fl_uploadstatus + ", fl_deptno=" + fl_deptno + "]";
	}
	
	
	
	

}
