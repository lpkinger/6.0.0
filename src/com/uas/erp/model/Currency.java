package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class Currency implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cu_id;
	private String cu_code;
	private String cu_value;
	private String cu_value_en;
	private String cu_value_tw;

	public int getCu_id() {
		return cu_id;
	}

	public void setCu_id(int cu_id) {
		this.cu_id = cu_id;
	}

	public String getCu_code() {
		return cu_code;
	}

	public void setCu_code(String cu_code) {
		this.cu_code = cu_code;
	}

	public String getCu_value() {
		return cu_value;
	}

	public void setCu_value(String cu_value) {
		this.cu_value = cu_value;
	}

	public String getCu_value_en() {
		return cu_value_en;
	}

	public void setCu_value_en(String cu_value_en) {
		this.cu_value_en = cu_value_en;
	}

	public String getCu_value_tw() {
		return cu_value_tw;
	}

	public void setCu_value_tw(String cu_value_tw) {
		this.cu_value_tw = cu_value_tw;
	}

	@Override
	public String table() {
		return "Currency";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "cu_id" };
	}

	public String getCurrencyValue(String language) {
		if (language.equals("en_US")) {
			return this.cu_value_en;
		} else if (language.equals("zh_TW")) {
			return this.cu_value_tw;
		} else {
			return this.cu_value;
		}
	}
}
