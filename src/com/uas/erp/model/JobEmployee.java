package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class JobEmployee implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int je_id;
	private String je_virtualMan;
	private String je_defaultMan;
	private String je_job;

	public int getJe_id() {
		return je_id;
	}

	public void setJe_id(int je_id) {
		this.je_id = je_id;
	}

	public String getJe_virtualMan() {
		return je_virtualMan;
	}

	public void setJe_virtualMan(String je_virtualMan) {
		this.je_virtualMan = je_virtualMan;
	}

	public String getJe_defaultMan() {
		return je_defaultMan;
	}

	public void setJe_defaultMan(String je_defaultMan) {
		this.je_defaultMan = je_defaultMan;
	}

	public String getJe_job() {
		return je_job;
	}

	public void setJe_job(String je_job) {
		this.je_job = je_job;
	}

	@Override
	public String table() {

		return "JobEmployee";
	}

	@Override
	public String[] keyColumns() {

		return new String[] { "je_id" };
	}

}
