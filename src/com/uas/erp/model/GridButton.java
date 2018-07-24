package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class GridButton implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int gb_id;
	private String gb_caller;
	private String gb_xtype;
	private String gb_url;
	private String gb_conf;
	
	public String getGb_conf() {
		return gb_conf;
	}

	public void setGb_conf(String gb_conf) {
		this.gb_conf = gb_conf;
	}

	public int getGb_id() {
		return gb_id;
	}

	public void setGb_id(int gb_id) {
		this.gb_id = gb_id;
	}

	public String getGb_caller() {
		return gb_caller;
	}

	public void setGb_caller(String gb_caller) {
		this.gb_caller = gb_caller;
	}

	public String getGb_xtype() {
		return gb_xtype;
	}

	public void setGb_xtype(String gb_xtype) {
		this.gb_xtype = gb_xtype;
	}

	public String getGb_url() {
		return gb_url;
	}

	public void setGb_url(String gb_url) {
		this.gb_url = gb_url;
	}

	@Override
	public String table() {
		return "GridButton";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "gb_id" };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gb_caller == null) ? 0 : gb_caller.hashCode());
		result = prime * result + gb_id;
		result = prime * result + ((gb_url == null) ? 0 : gb_url.hashCode());
		result = prime * result + ((gb_xtype == null) ? 0 : gb_xtype.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridButton other = (GridButton) obj;
		if (gb_caller == null) {
			if (other.gb_caller != null)
				return false;
		} else if (!gb_caller.equals(other.gb_caller))
			return false;
		if (gb_id != other.gb_id)
			return false;
		if (gb_url == null) {
			if (other.gb_url != null)
				return false;
		} else if (!gb_url.equals(other.gb_url))
			return false;
		if (gb_xtype == null) {
			if (other.gb_xtype != null)
				return false;
		} else if (!gb_xtype.equals(other.gb_xtype))
			return false;
		return true;
	}

}
