package com.uas.erp.model;

import java.io.Serializable;

public class AgendaType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int at_id;
	private String at_name;
	private String at_color;

	public int getAt_id() {
		return at_id;
	}

	public void setAt_id(int at_id) {
		this.at_id = at_id;
	}

	public String getAt_name() {
		return at_name;
	}

	public void setAt_name(String at_name) {
		this.at_name = at_name;
	}

	public String getAt_color() {
		return at_color;
	}

	public void setAt_color(String at_color) {
		this.at_color = at_color;
	}

}
