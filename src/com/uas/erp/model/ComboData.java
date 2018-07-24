package com.uas.erp.model;

import java.io.Serializable;

public class ComboData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String display;
	private String value;

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ComboData() {

	}

	public ComboData(String str) {
		this.display = str;
		// if(str.equals("S") || str.equals("text")){
		// this.display = "字符串";
		// } else if(str.equals("D") || str.equals("datecolumn")){
		// this.display = "日期";
		// } else if(str.equals("N") || str.equals("numbercolumn")){
		// this.display = "数字";
		// } else if(str.equals("T")){
		// this.display = "文本框";
		// } else if(str.equals("H")){
		// this.display = "隐藏域";
		// } else if(str.equals("C") || str.equals("combo")){
		// this.display = "下拉列表";
		// } else if(str.equals("no") || str.equals("-1")){
		// this.display = "是";
		// } else if(str.equals("yes") || str.equals("0")){
		// this.display = "否";
		// }
		this.value = str;
	}

	public ComboData(String display, String value) {
		this.display = display;
		this.value = value;
	}
}
