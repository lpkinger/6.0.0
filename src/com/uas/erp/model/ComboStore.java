package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ComboStore implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> fields;
	private List<ComboData> data;

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public List<ComboData> getData() {
		return data;
	}

	public void setData(List<ComboData> data) {
		this.data = data;
	}

	public ComboStore() {

	}
	/**
	 * 关联查询中设置下拉框的值
	 * @author lidy
	 * @param combovalue  display1:value1;display2:value2
	 */
	public ComboStore(String combovalue){
		List<String> fields = new ArrayList<String>();
		fields.add("display");
		fields.add("value");
		this.fields = fields;
		List<ComboData> comboDatas = new ArrayList<ComboData>();
		if(combovalue!=null&&!combovalue.equals("")){
			String[] combos = combovalue.split(";");
			for(String combo: combos){
				String display,value;
				if(combo.indexOf(":")!=-1){
					display = combo.substring(0, combo.indexOf(":"));
					value = combo.substring(combo.indexOf(":")+1);
				}else{
					display = combo;
					value = combo;
				}
				comboDatas.add(new ComboData(display, value));
			}
		}
		this.data = comboDatas;
	}

	public ComboStore(List<DataListCombo> combos, String dataIndex, String language) {
		List<String> fields = new ArrayList<String>();
		fields.add("display");
		fields.add("value");
		this.fields = fields;
		List<ComboData> comboDatas = new ArrayList<ComboData>();
		for (DataListCombo com : combos) {
			if (com.getDlc_fieldname().equals(dataIndex)) {
				String value = com.getDlc_value();
				if (language.equals("en_US")) {
					value = com.getDlc_value_en();
				} else if (language.equals("zh_TW")) {
					value = com.getDlc_value_tw();
				}
				comboDatas.add(new ComboData(value, com.getDlc_display()));// 有些不需要存储为code的，如进行是否判断的字段，值取-1、0即可
			}
		}
		this.data = comboDatas;
	}

	public static ComboStore yesnoCombo(String dataIndex) {
		ComboStore combo = new ComboStore();
		List<String> fields = new ArrayList<String>();
		fields.add("display");
		fields.add("value");
		combo.setFields(fields);
		List<ComboData> datas = new ArrayList<ComboData>();
		datas.add(new ComboData("是", "-1"));
		datas.add(new ComboData("否", "0"));
		combo.setData(datas);
		return combo;
	}
}
