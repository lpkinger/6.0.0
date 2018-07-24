package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class DataDictionary implements Serializable {

	/**
	 * 采用oracle自带视图
	 */
	private static final long serialVersionUID = -1244023651173277407L;
	
	private String table_name;
	
	private String comments;
	
	private List<DataDictionaryDetail> dataDictionaryDetails;
	
	public String getTable_name() {
		return table_name;
	}
	
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


	public List<DataDictionaryDetail> getDataDictionaryDetails() {
		return dataDictionaryDetails;
	}

	public void setDataDictionaryDetails(List<DataDictionaryDetail> dataDictionaryDetails) {
		this.dataDictionaryDetails = dataDictionaryDetails;
	}
}
