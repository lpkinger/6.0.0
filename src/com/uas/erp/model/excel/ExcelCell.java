package com.uas.erp.model.excel;

import java.io.Serializable;

public class ExcelCell implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5792445550748442622L;

	private Integer cellid;
	private Integer cellsheetid;
	private Integer cellrow;
	private Integer cellcol;
	private String cellcontent;
	private Boolean cellcal;
	private String cellrawdata;
	private String cellcalvalue;
	
	public ExcelCell() {
		
	}
	
	public ExcelCell(int cellsheetid_tpl, int cellrow_tpl, int cellcol_tpl, String cellcontent_tpl,
			Boolean cellcal_tpl, String cellrawdata_tpl) {
		super();
		this.cellsheetid = cellsheetid_tpl;
		this.cellrow = cellrow_tpl;
		this.cellcol = cellcol_tpl;
		this.cellcontent = cellcontent_tpl;
		this.cellcal = cellcal_tpl;
		this.cellrawdata = cellrawdata_tpl;
	}

	

	public Integer getCellid() {
		return cellid;
	}

	public void setCellid(Integer cellid) {
		this.cellid = cellid;
	}

	public Integer getCellsheetid() {
		return cellsheetid;
	}

	public void setCellsheetid(Integer cellsheetid) {
		this.cellsheetid = cellsheetid;
	}

	public Integer getCellrow() {
		return cellrow;
	}

	public void setCellrow(Integer cellrow) {
		this.cellrow = cellrow;
	}

	public Integer getCellcol() {
		return cellcol;
	}

	public void setCellcol(Integer cellcol) {
		this.cellcol = cellcol;
	}

	public String getCellcontent() {
		return cellcontent;
	}

	public void setCellcontent(String cellcontent) {
		this.cellcontent = cellcontent;
	}

	public Boolean getCellcal() {
		return cellcal;
	}

	public void setCellcal(Boolean cellcal) {
		this.cellcal = cellcal;
	}

	public String getCellrawdata() {
		return cellrawdata;
	}

	public void setCellrawdata(String cellrawdata) {
		this.cellrawdata = cellrawdata;
	}


	public String getCellcalvalue() {
		return cellcalvalue;
	}

	public void setCellcalvalue(String cellcalvalue) {
		this.cellcalvalue = cellcalvalue;
	}

	@Override
	public String toString() {
		return "ExcelCell [cellid=" + cellid + ", cellsheetid=" + cellsheetid + ", cellrow=" + cellrow + ", cellcol="
				+ cellcol + ", cellcontent=" + cellcontent + ", cellcal=" + cellcal + ", cellrawdata=" + cellrawdata
				+ ", cellcalvalue=" + cellcalvalue + "]";
	}

	
	
	
	
}
