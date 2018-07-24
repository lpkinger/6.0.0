package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.dao.Saveable;

public class Sign implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int si_id;
	private int si_emid;
	private String si_emcode;
	private String si_emname;
	private Date si_in;
	private Date si_out;
	private String si_inreason;
	private String si_outreason;

	public int getSi_id() {
		return si_id;
	}

	public void setSi_id(int si_id) {
		this.si_id = si_id;
	}

	public int getSi_emid() {
		return si_emid;
	}

	public void setSi_emid(int si_emid) {
		this.si_emid = si_emid;
	}

	public String getSi_emcode() {
		return si_emcode;
	}

	public void setSi_emcode(String si_emcode) {
		this.si_emcode = si_emcode;
	}

	public String getSi_emname() {
		return si_emname;
	}

	public void setSi_emname(String si_emname) {
		this.si_emname = si_emname;
	}

	public Date getSi_in() {
		return si_in;
	}

	public void setSi_in(Date si_in) {
		this.si_in = si_in;
	}

	public Date getSi_out() {
		return si_out;
	}

	public void setSi_out(Date si_out) {
		this.si_out = si_out;
	}

	public String getSi_inreason() {
		return si_inreason;
	}

	public void setSi_inreason(String si_inreason) {
		this.si_inreason = si_inreason;
	}

	public String getSi_outreason() {
		return si_outreason;
	}

	public void setSi_outreason(String si_outreason) {
		this.si_outreason = si_outreason;
	}

	@Override
	public String table() {
		return "Sign";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "si_id" };
	}
}
