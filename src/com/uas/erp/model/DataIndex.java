package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataIndex implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String index_name;
	private String uniqueness;
	private List<Map<String,Object>> ind_columns;
	public String getIndex_name() {
		return index_name;
	}
	public void setIndex_name(String index_name) {
		this.index_name = index_name;
	}
	public String getUniqueness() {
		return uniqueness;
	}
	public void setUniqueness(String uniqueness) {
		this.uniqueness = uniqueness;
	}
	public List<Map<String, Object>> getInd_columns() {
		return ind_columns;
	}
	public void setInd_columns(List<Map<String, Object>> ind_columns) {
		this.ind_columns = ind_columns;
	}

}
