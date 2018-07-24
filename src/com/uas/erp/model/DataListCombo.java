package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 下拉框combox配置表
 */
public class DataListCombo implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer dlc_id;
	private String dlc_caller;
	private String dlc_fieldname;
	private String dlc_display;
	private String dlc_value;
	private String dlc_value_en;
	private String dlc_value_tw;
	private Number dlc_detno;

	public DataListCombo() {
	}

	public DataListCombo(DataListCombo combo, String aliaField) {
		this.dlc_fieldname = aliaField;
		this.dlc_display = combo.getDlc_display();
		this.dlc_value = combo.getDlc_value();
	}

	public Integer getDlc_id() {
		return dlc_id;
	}

	public void setDlc_id(Integer dlc_id) {
		this.dlc_id = dlc_id;
	}

	public String getDlc_caller() {
		return dlc_caller;
	}

	public void setDlc_caller(String dlc_caller) {
		this.dlc_caller = dlc_caller;
	}

	public String getDlc_fieldname() {
		return dlc_fieldname;
	}

	public void setDlc_fieldname(String dlc_fieldname) {
		this.dlc_fieldname = dlc_fieldname;
	}

	public String getDlc_display() {
		return dlc_display;
	}

	public void setDlc_display(String dlc_display) {
		this.dlc_display = dlc_display;
	}

	public String getDlc_value() {
		return dlc_value;
	}

	public void setDlc_value(String dlc_value) {
		this.dlc_value = dlc_value;
	}

	public String getDlc_value_en() {
		return dlc_value_en;
	}

	public void setDlc_value_en(String dlc_value_en) {
		this.dlc_value_en = dlc_value_en;
	}

	public String getDlc_value_tw() {
		return dlc_value_tw;
	}

	public void setDlc_value_tw(String dlc_value_tw) {
		this.dlc_value_tw = dlc_value_tw;
	}

	@Override
	public String table() {
		return "dataListCombo";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "dlc_id" };
	}

	public Number getDlc_detno() {
		return dlc_detno;
	}

	public void setDlc_detno(Number dlc_detno) {
		this.dlc_detno = dlc_detno;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dlc_caller == null) ? 0 : dlc_caller.hashCode());
		result = prime * result + ((dlc_detno == null) ? 0 : dlc_detno.hashCode());
		result = prime * result + ((dlc_display == null) ? 0 : dlc_display.hashCode());
		result = prime * result + ((dlc_fieldname == null) ? 0 : dlc_fieldname.hashCode());
		result = prime * result + ((dlc_id == null) ? 0 : dlc_id.hashCode());
		result = prime * result + ((dlc_value == null) ? 0 : dlc_value.hashCode());
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
		DataListCombo other = (DataListCombo) obj;
		if (dlc_caller == null) {
			if (other.dlc_caller != null)
				return false;
		} else if (!dlc_caller.equals(other.dlc_caller))
			return false;
		if (dlc_detno == null) {
			if (other.dlc_detno != null)
				return false;
		} else if (!dlc_detno.equals(other.dlc_detno))
			return false;
		if (dlc_display == null) {
			if (other.dlc_display != null)
				return false;
		} else if (!dlc_display.equals(other.dlc_display))
			return false;
		if (dlc_fieldname == null) {
			if (other.dlc_fieldname != null)
				return false;
		} else if (!dlc_fieldname.equals(other.dlc_fieldname))
			return false;
		if (dlc_id == null) {
			if (other.dlc_id != null)
				return false;
		} else if (!dlc_id.equals(other.dlc_id))
			return false;
		if (dlc_value == null) {
			if (other.dlc_value != null)
				return false;
		} else if (!dlc_value.equals(other.dlc_value))
			return false;
		return true;
	}
}
