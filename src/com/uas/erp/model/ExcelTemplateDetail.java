package com.uas.erp.model;

import java.io.Serializable;

import net.sf.json.JSONObject;

public class ExcelTemplateDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String etd_sheetname;
	private int etd_rowindex;
	private int etd_colindex;
	private String etd_data;
	private int etd_mainid;

	public String getEtd_sheetname() {
		return etd_sheetname;
	}

	public void setEtd_sheetname(String etd_sheetname) {
		this.etd_sheetname = etd_sheetname;
	}

	public int getEtd_rowindex() {
		return etd_rowindex;
	}

	public void setEtd_rowindex(int etd_rowindex) {
		this.etd_rowindex = etd_rowindex;
	}

	public int getEtd_colindex() {
		return etd_colindex;
	}

	public void setEtd_colindex(int etd_colindex) {
		this.etd_colindex = etd_colindex;
	}

	public String getEtd_data() {
		return etd_data;
	}

	public void setEtd_data(String etd_data) {
		this.etd_data = etd_data;
	}

	public int getEtd_mainid() {
		return etd_mainid;
	}

	public void setEtd_mainid(int etd_mainid) {
		this.etd_mainid = etd_mainid;
	}

	public JSONObject getJsonData() {
		return JSONObject.fromObject(this.etd_data);
	}

	public String getField() {
		String data = this.getJsonData().getString("data");
		if (data.contains("&")) {
			String str = data.split("&")[0];
			return str.substring(str.indexOf("#") + 1);
		} else
			return "";
	}

	public String getCaption() {
		String data = this.getJsonData().getString("data");
		if (data.contains("&")) {
			String str = data.split("&")[1];
			str = str.replace("amp;", "");
			return str;
		} else
			return "";
	}

	// 获得函数名
	public String getFxname() {
		String data = this.getData();
		String str = data.split("[(]")[0];
		return str.substring(3, str.length());
	}

	// 获得函数描述
	public String getFxcaption() {
		String data = this.getData();
		if (data.split("&").length > 1) {
			String str = data.split("&")[1].replace("amp;", "");
			return str;
		}
		return "";
	}

	// 获得参数
	public String getFxArgs() {
		String data = this.getData();
		return data.substring(data.indexOf("(") + 1, data.lastIndexOf(")"));
	}

	public String getData() {
		return JSONObject.fromObject(this.etd_data).getString("data");
	}

	// 返回完整的数据
	public JSONObject getRealData(Object value) {
		JSONObject json = this.getJsonData();
		json.remove("data");
		json.put("d", value);
		return json;
	}

	public boolean checkCondition(int x, int y) {
		return this.getEtd_rowindex() == x && this.getEtd_colindex() == y;
	}
}
