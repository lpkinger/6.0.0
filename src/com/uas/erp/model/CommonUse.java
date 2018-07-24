package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 常用模块
 */
public class CommonUse implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cu_id;
	private String cu_description;
	private int cu_snid;
	private int cu_count;
	private int cu_emid;
	private String cu_url;
	private Number cu_lock=0;
	private String cu_caller;
	private String cu_addUrl;

	public int getCu_id() {
		return cu_id;
	}

	public void setCu_id(int cu_id) {
		this.cu_id = cu_id;
	}

	public String getCu_description() {
		return cu_description;
	}

	public void setCu_description(String cu_description) {
		this.cu_description = cu_description;
	}

	public int getCu_snid() {
		return cu_snid;
	}

	public void setCu_snid(int cu_snid) {
		this.cu_snid = cu_snid;
	}

	public int getCu_count() {
		return cu_count;
	}

	public void setCu_count(int cu_count) {
		this.cu_count = cu_count;
	}

	public int getCu_emid() {
		return cu_emid;
	}

	public void setCu_emid(int cu_emid) {
		this.cu_emid = cu_emid;
	}

	public String getCu_url() {
		return cu_url;
	}

	public void setCu_url(String cu_url) {
		this.cu_url = cu_url;
	}
	
	public String getCu_addUrl() {
		return cu_addUrl;
	}
	
	public void setCu_addUrl(String cu_addUrl) {
		this.cu_addUrl = cu_addUrl;
	}

	@Override
	public String table() {
		return "CommonUse";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "cu_id" };
	}

	public CommonUse() {

	}

	public CommonUse(String cu_description, int cu_snid, int cu_emid, String cu_url, String cu_addUrl,String cu_caller) {
		this.cu_description = cu_description;
		this.cu_snid = cu_snid;
		this.cu_emid = cu_emid;
		this.cu_count = 1;
		this.cu_url = cu_url;
		this.cu_addUrl = cu_addUrl;
		this.cu_caller = cu_caller;
	}
	public Number getCu_lock() {
		return cu_lock;
	}

	public void setCu_lock(Number cu_lock) {
		this.cu_lock = cu_lock;
	}

	public String getCu_caller() {
		return cu_caller;
	}

	public void setCu_caller(String cu_caller) {
		this.cu_caller = cu_caller;
	}

}
