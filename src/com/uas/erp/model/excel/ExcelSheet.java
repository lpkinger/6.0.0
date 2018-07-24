package com.uas.erp.model.excel;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExcelSheet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3768572070370301288L;
	
	
	private Integer sheetid;
	
	 
	private Integer sheetfileid;

	private String sheetname;

	private Integer sheetorder;
	
	private Boolean sheetactive;
	
	private Boolean sheethidden;
	
	private String sheetextrainfo;
	 
	private String sheetcelltable;
	 
	private String sheetcolor;
	
	private Integer sheetwidth;

	public ExcelSheet() {
	}
	
	

	public ExcelSheet(int sheetid, int sheetfileid, String sheetname, int sheetorder,
			Boolean sheetactive, String sheetcellsheetle_tpl) {
		super();
		this.sheetid = sheetid;
		this.sheetfileid = sheetfileid;
		this.sheetname = sheetname;
		this.sheetorder = sheetorder;
		this.sheetactive = sheetactive;
		this.sheetcelltable = sheetcellsheetle_tpl;
	}
	
	public Integer getsheetid() {
		return sheetid;
	}

	public void setsheetid(Integer sheetid) {
		this.sheetid = sheetid;
	}

	public Integer getsheetfileid() {
		return sheetfileid;
	}

	public void setsheetfileid(Integer sheetfileid) {
		this.sheetfileid = sheetfileid;
	}

	public String getsheetname() {
		return sheetname;
	}

	public void setsheetname(String sheetname) {
		this.sheetname = sheetname;
	}

	public Integer getsheetorder() {
		return sheetorder;
	}

	public void setsheetorder(Integer sheetorder) {
		this.sheetorder = sheetorder;
	}

	public Boolean getsheetactive() {
		return sheetactive;
	}

	public void setsheetactive(Boolean sheetactive) {
		this.sheetactive = sheetactive;
	}

	public Boolean getsheethidden() {
		return sheethidden;
	}

	public void setsheethidden(Boolean sheethidden) {
		this.sheethidden = sheethidden;
	}

	public String getsheetextrainfo() {
		return sheetextrainfo;
	}

	public void setsheetextrainfo(String sheetextrainfo) {
		this.sheetextrainfo = sheetextrainfo;
	}

	public String getsheetcelltable() {
		return sheetcelltable;
	}

	public void setsheetcelltable(String sheetcelltable) {
		this.sheetcelltable = sheetcelltable;
	}

	public String getsheetcolor() {
		return sheetcolor;
	}

	public void setsheetcolor(String sheetcolor) {
		this.sheetcolor = sheetcolor;
	}

	public Integer getsheetwidth() {
		return sheetwidth;
	}

	public void setsheetwidth(Integer sheetwidth) {
		this.sheetwidth = sheetwidth;
	}

	@Override
	public String toString() {
		return "ExcelSheet_Template [sheetid=" + sheetid + ", sheetfileid=" + sheetfileid
				+ ", sheetname=" + sheetname + ", sheetorder=" + sheetorder + ", sheetactive="
				+ sheetactive + ", sheethidden=" + sheethidden + ", sheetextrainfo="
				+ sheetextrainfo + ", sheetcellsheetle_tpl=" + sheetcelltable + ", sheetcolor="
				+ sheetcolor + ", sheetwidth=" + sheetwidth + "]";
	}
	
	
	
	
}
