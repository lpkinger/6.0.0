package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

public class NewsComment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int nc_id;
	private int nc_neid;
	private String nc_caster;
	private String nc_date;
	private String nc_comment;

	public int getNc_id() {
		return nc_id;
	}

	public void setNc_id(int nc_id) {
		this.nc_id = nc_id;
	}

	public int getNc_neid() {
		return nc_neid;
	}

	public void setNc_neid(int nc_neid) {
		this.nc_neid = nc_neid;
	}

	public String getNc_caster() {
		return nc_caster;
	}

	public void setNc_caster(String nc_caster) {
		this.nc_caster = nc_caster;
	}

	public String getNc_date() {
		return nc_date;
	}

	public void setNc_date(String nc_date) {
		this.nc_date = nc_date.substring(0, nc_date.length() - 2);
	}

	public String getNc_comment() {
		return nc_comment;
	}

	public void setNc_comment(String nc_comment) {
		this.nc_comment = nc_comment;
	}

	public String getNc_datestr() {
		return DateUtil.parseDateToString(DateUtil.parseStringToDate(nc_date, Constant.YMD_HMS), "MM月dd日 HH:mm");
	}

}
