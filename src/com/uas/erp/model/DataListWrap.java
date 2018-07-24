package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class DataListWrap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataList dataList;
	/**
	 * 关联列表
	 */
	private DataListWrap relative;
	private List<DataListCombo> combos;

	public DataList getDataList() {
		return dataList;
	}

	public void setDataList(DataList dataList) {
		this.dataList = dataList;
	}

	public DataListWrap getRelative() {
		return relative;
	}

	public void setRelative(DataListWrap relative) {
		this.relative = relative;
	}

	public List<DataListCombo> getCombos() {
		return combos;
	}

	public void setCombos(List<DataListCombo> combos) {
		this.combos = combos;
	}

}
