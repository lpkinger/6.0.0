package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.Saveable;

public class PagingRelease implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5690255264466441889L;
	private int pr_id;
	private String pr_releaser;
	private Date pr_date;
	private int pr_releaserid;
	private String pr_context;
	private String pr_from;
	private String pr_codevalue;
	private String pr_caller;
	private String pr_title;
	private int pr_status;
	private Integer pr_istop;
	private Integer pr_keyvalue;

	public int getPr_id() {
		return pr_id;
	}

	public void setPr_id(int pr_id) {
		this.pr_id = pr_id;
	}

	public String getPr_releaser() {
		return pr_releaser;
	}

	public void setPr_releaser(String pr_releaser) {
		this.pr_releaser = pr_releaser;
	}

	public Date getPr_date() {
		return pr_date;
	}

	public void setPr_date(Date pr_date) {
		this.pr_date = pr_date;
	}

	public int getPr_releaserid() {
		return pr_releaserid;
	}

	public void setPr_releaserid(int pr_releaserid) {
		this.pr_releaserid = pr_releaserid;
	}

	public String getPr_context() {
		return pr_context;
	}

	public void setPr_context(String pr_context) {
		this.pr_context = pr_context;
	}

	public String getPr_from() {
		return pr_from;
	}

	public void setPr_from(String pr_from) {
		this.pr_from = pr_from;
	}

	public String getPr_codevalue() {
		return pr_codevalue;
	}

	public void setPr_codevalue(String pr_codevalue) {
		this.pr_codevalue = pr_codevalue;
	}

	public String getPr_caller() {
		return pr_caller;
	}

	public void setPr_caller(String pr_caller) {
		this.pr_caller = pr_caller;
	}

	public String getPr_title() {
		return pr_title;
	}

	public void setPr_title(String pr_title) {
		this.pr_title = pr_title;
	}

	public int getPr_status() {
		return pr_status;
	}

	public void setPr_status(int pr_status) {
		this.pr_status = pr_status;
	}
	
	public Integer getPr_istop() {
		return pr_istop;
	}

	public void setPr_istop(Integer pr_istop) {
		this.pr_istop = pr_istop;
	}

	public Integer getPr_keyvalue() {
		return pr_keyvalue;
	}

	public void setPr_keyvalue(Integer pr_keyvalue) {
		this.pr_keyvalue = pr_keyvalue;
	}

	public PagingRelease(int pr_id, String pr_releaser, Date pr_date, int pr_releaserid, String pr_context, String pr_from,
			String pr_codevalue, String pr_caller, String pr_title) {
		this.pr_id = pr_id;
		this.pr_releaser = pr_releaser;
		this.pr_date = pr_date;
		this.pr_releaserid = pr_releaserid;
		this.pr_context = pr_context;
		this.pr_from = pr_from;
		this.pr_codevalue = pr_codevalue;
		this.pr_caller = pr_caller;
		this.pr_title = pr_title;
		this.pr_status = 0;
	}
	public PagingRelease(int pr_id, String pr_releaser, Date pr_date, int pr_releaserid, String pr_context, String pr_from,
			String pr_codevalue,Integer pr_keyvalue, String pr_caller, String pr_title) {
		this.pr_id = pr_id;
		this.pr_releaser = pr_releaser;
		this.pr_date = pr_date;
		this.pr_releaserid = pr_releaserid;
		this.pr_context = pr_context;
		this.pr_from = pr_from;
		this.pr_codevalue = pr_codevalue;
		this.pr_keyvalue = pr_keyvalue;
		this.pr_caller = pr_caller;
		this.pr_title = pr_title;
		this.pr_status = 0;
	}
	public PagingRelease(int pr_id,String title,String context,Employee releaser) {
		this.pr_id=pr_id;
		this.pr_title=title;
		this.pr_status = 0;
		this.pr_context=context;
		this.pr_date=new Date();
		this.pr_istop=1;
		this.pr_releaserid=releaser.getEm_id();
		this.pr_releaser=releaser.getEm_name();		
	}
	public PagingRelease(int pr_id,String title,String context,Employee releaser,String pr_from) {
		this.pr_id=pr_id;
		this.pr_title=title;
		this.pr_status = 0;
		this.pr_context=context;
		this.pr_date=new Date();
		this.pr_istop=1;
		this.pr_from = pr_from;
		this.pr_releaserid=releaser.getEm_id();
		this.pr_releaser=releaser.getEm_name();		
	}
	@Override
	public String table() {
		// TODO Auto-generated method stub
		return "PagingRelease";
	}

	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return null;
	}

}
