package com.uas.erp.model.excel;

import java.io.Serializable;

public class ExcelElement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2441904204063374878L;

	private Integer eleid;
	private Integer elesheetid;
	private String elename;
	private String eletype;
	private String elecontent;
	
	public ExcelElement() {
	}

	public ExcelElement(String elename, String eletype, String elecontent) {
		super();
		this.elename = elename;
		this.eletype = eletype;
		this.elecontent = elecontent;
	}

	public Integer getEleid() {
		return eleid;
	}

	public void setEleid(Integer eleid) {
		this.eleid = eleid;
	}

	public Integer getElesheetid() {
		return elesheetid;
	}

	public void setElesheetid(Integer elesheetid) {
		this.elesheetid = elesheetid;
	}

	public String getElename() {
		return elename;
	}

	public void setElename(String elename) {
		this.elename = elename;
	}

	public String getEletype() {
		return eletype;
	}

	public void setEletype(String eletype) {
		this.eletype = eletype;
	}

	public String getElecontent() {
		return elecontent;
	}

	public void setElecontent(String elecontent) {
		this.elecontent = elecontent;
	}

	@Override
	public String toString() {
		return "ExcelElement [eleid=" + eleid + ", elesheetid=" + elesheetid + ", elename=" + elename + ", eletype="
				+ eletype + ", elecontent=" + elecontent + "]";
	}

	
	
	
	
	
	
}
