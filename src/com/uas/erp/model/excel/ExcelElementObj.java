package com.uas.erp.model.excel;

public class ExcelElementObj {
	
	private Integer id;
	private Integer sheet;	
	private String name;	
	private String ftype;
	private String json;
	
	public ExcelElementObj(ExcelElement element) {
		this.id = element.getEleid();
		this.sheet = element.getElesheetid();
		this.name = element.getElename();
		this.json = element.getElecontent();
		this.ftype = element.getEletype();
	}
	
	public ExcelElementObj(Integer sheetId, String json) {
		this.sheet = sheetId;
		this.json = json;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSheet() {
		return sheet;
	}

	public void setSheet(Integer sheet) {
		this.sheet = sheet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFtype() {
		return ftype;
	}

	public void setFtype(String ftype) {
		this.ftype = ftype;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	
}

