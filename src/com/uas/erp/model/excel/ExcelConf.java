package com.uas.erp.model.excel;

import java.io.Serializable;

public class ExcelConf implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 107122083407326699L;

	private Integer confid;
	private Integer confileid;
	private String confname;
	private String conftype;
	private String confcontent;
	
	public ExcelConf() {
	}

	

	public Integer getConfid() {
		return confid;
	}



	public void setConfid(Integer confid) {
		this.confid = confid;
	}



	public Integer getConfileid() {
		return confileid;
	}



	public void setConfileid(Integer confileid) {
		this.confileid = confileid;
	}



	public String getConfname() {
		return confname;
	}



	public void setConfname(String confname) {
		this.confname = confname;
	}



	public String getConftype() {
		return conftype;
	}



	public void setConftype(String conftype) {
		this.conftype = conftype;
	}



	public String getConfcontent() {
		return confcontent;
	}



	public void setConfcontent(String confcontent) {
		this.confcontent = confcontent;
	}



	@Override
	public String toString() {
		return "ExcelConf [confid=" + confid + ", confileid=" + confileid + ", confname=" + confname + ", conftype="
				+ conftype + ", confcontent=" + confcontent + "]";
	}

	
	
}
