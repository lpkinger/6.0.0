package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.support.KeyEntity;

/**
 * erp label模型
 * @author aof
 * @date 2015年10月28日
 */
public class Label extends KeyEntity {
	
	private Long la_id;
	private String la_name;
	private Date la_indate;
	private String la_inman;
	private String la_status;
	private String la_pagesize;
	private String la_code;
	private String la_statuscode;
	private String la_sendstatus;

	public Long getLa_id() {
		return la_id;
	}

	public void setLa_id(Long la_id) {
		this.la_id = la_id;
	}

	public String getLa_name() {
		return la_name;
	}

	public void setLa_name(String la_name) {
		this.la_name = la_name;
	}

	public Date getLa_indate() {
		return la_indate;
	}

	public void setLa_indate(Date la_indate) {
		this.la_indate = la_indate;
	}

	public String getLa_inman() {
		return la_inman;
	}

	public void setLa_inman(String la_inman) {
		this.la_inman = la_inman;
	}

	public String getLa_status() {
		return la_status;
	}

	public void setLa_status(String la_status) {
		this.la_status = la_status;
	}

	public String getLa_pagesize() {
		return la_pagesize;
	}

	public void setLa_pagesize(String la_pagesize) {
		this.la_pagesize = la_pagesize;
	}

	public String getLa_code() {
		return la_code;
	}

	public void setLa_code(String la_code) {
		this.la_code = la_code;
	}

	public String getLa_statuscode() {
		return la_statuscode;
	}

	public void setLa_statuscode(String la_statuscode) {
		this.la_statuscode = la_statuscode;
	}
	
	public String getLa_sendstatus() {
		return la_sendstatus;
	}

	public void setLa_sendstatus(String la_sendstatus) {
		this.la_sendstatus = la_sendstatus;
	}

	@Override
	public Object getKey() {
		return this.la_id ;
	}
	
}
