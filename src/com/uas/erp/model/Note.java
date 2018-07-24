package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int no_id;
	private String no_title;
	private Date  no_apptime;
    private String no_approver;
    private String no_content;
	public int getNo_id() {
		return no_id;
	}
	public void setNo_id(int no_id) {
		this.no_id = no_id;
	}
	public String getNo_title() {
		return no_title;
	}
	public void setNo_title(String no_title) {
		this.no_title = no_title;
	}
	public Date getNo_apptime() {
		return no_apptime;
	}
	public void setNo_apptime(Date no_apptime) {
		this.no_apptime = no_apptime;
	}
	public String getNo_approver() {
		return no_approver;
	}
	public void setNo_approver(String no_approver) {
		this.no_approver = no_approver;
	}
	public String getNo_content() {
		return no_content;
	}
	public void setNo_content(String no_content) {
		this.no_content = no_content;
	}
    

}
