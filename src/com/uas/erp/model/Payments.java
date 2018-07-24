package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class Payments implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pa_id;
	private String pa_code;
	private String pa_value;
	private String pa_value_en;
	private String pa_value_tw;

	public int getPa_id() {
		return pa_id;
	}

	public void setPa_id(int pa_id) {
		this.pa_id = pa_id;
	}

	public String getPa_code() {
		return pa_code;
	}

	public void setPa_code(String pa_code) {
		this.pa_code = pa_code;
	}

	public String getPa_value() {
		return pa_value;
	}

	public void setPa_value(String pa_value) {
		this.pa_value = pa_value;
	}

	public String getPa_value_en() {
		return pa_value_en;
	}

	public void setPa_value_en(String pa_value_en) {
		this.pa_value_en = pa_value_en;
	}

	public String getPa_value_tw() {
		return pa_value_tw;
	}

	public void setPa_value_tw(String pa_value_tw) {
		this.pa_value_tw = pa_value_tw;
	}

	@Override
	public String table() {
		return "Payments";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "pa_id" };
	}

	public String getPaymentsValue(String language) {
		if (language.equals("en_US")) {
			return this.pa_value_en;
		} else if (language.equals("zh_TW")) {
			return this.pa_value_tw;
		} else {
			return this.pa_value;
		}
	}
}
