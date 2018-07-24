package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class Editor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String xtype = "textfield";
	private String format = "";
	private boolean hideTrigger = true;
	private ComboStore store;
	private String queryMode = "local";
	private String displayField = "display";
	private String valueField = "value";
	private boolean editable = true;
	private String cls;
	private String minValue;
	private boolean PositiveNum = false;
	private Integer maxLength = 4000;

	private boolean allowDecimals=true;
	
	public String getXtype() {
		return xtype;
	}

	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		if (minValue != null && minValue.equals(">0")) {
			this.PositiveNum = true;
			minValue = null;
		}
		this.minValue = minValue;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isHideTrigger() {
		return hideTrigger;
	}

	public void setHideTrigger(boolean hideTrigger) {
		this.hideTrigger = hideTrigger;
	}

	public ComboStore getStore() {
		return store;
	}

	public void setStore(ComboStore store) {
		this.store = store;
	}

	public String getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public boolean isPositiveNum() {
		return PositiveNum;
	}

	public void setPositiveNum(boolean positiveNum) {
		PositiveNum = positiveNum;
	}

	public Editor() {

	}

	public Editor(String type) {
		if (type.equals("numbercolumn")) {
			this.xtype = "numberfield";
			this.format = "0";
			this.hideTrigger = true;
		} else if (type.equals("floatcolumn")) {
			this.xtype = "numberfield";
			this.format = "0.00";
			this.hideTrigger = true;
		} else if (type.matches("^floatcolumn([0-9]|10){1}$")) {// ^floatcolumn\\d{1}$
			this.xtype = "numberfield";
			this.format = "0.";
			int length = Integer.parseInt(type.replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
			this.hideTrigger = true;
		} else if(type.equals("integer")){
			this.xtype = "numberfield";
			this.format = "0";
			this.hideTrigger = true;
			this.setAllowDecimals(false);
		}else if (type.equals("datecolumn")) {
			this.xtype = "datefield";
			this.format = "Y-m-d";
			this.hideTrigger = false;
		} else if (type.equals("datetimecolumn")) {
			this.xtype = "datetimefield";
			this.format = "Y-m-d H:i:s";
			this.hideTrigger = false;
		} else if (type.equals("timecolumn")) {
			this.xtype = "timefield";
			this.hideTrigger = false;
			this.format = "H:i";
		} else if (type.equals("monthcolumn")) {
			this.xtype = "monthdatefield";
			this.hideTrigger = false;
		} else if (type.equals("textcolumn") || type.equals("textfield") || type.equals("text")) {
			this.xtype = "textfield";
		} else if (type.equals("textareafield")) {
			this.xtype = "textareafield";
		} else if (type.equals("textareatrigger")) {
			this.xtype = "textareatrigger";
			this.hideTrigger = false;
		} else if (type.equals("dbfindtrigger")) {
			this.xtype = "dbfindtrigger";
			this.hideTrigger = false;
		} else if (type.equals("multidbfindtrigger")) {
			this.xtype = "multidbfindtrigger";
			this.hideTrigger = false;
		} else if (type.equals("datehourminutefield")) {
			this.xtype = "datehourminutefield";
			this.hideTrigger = false;
		} else if (type.equals("checkbox")) {
			this.xtype = "checkbox";
			this.cls = "x-grid-checkheader-editor";
			this.hideTrigger = false;
		} else {
			this.xtype = type;
			this.hideTrigger = false;
		}
	}

	public Editor(String type, boolean editable) {
		this(type);
		this.editable = editable;
	}

	public Editor(String field, List<DataListCombo> combos, String language) {
		this.xtype = "combo";
		this.store = new ComboStore(combos, field, language);
		this.hideTrigger = false;
		this.editable = false;
	}

	public Editor(String field, List<DataListCombo> combos, String language, boolean editable) {
		this(field, combos, language);
		this.editable = editable;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isAllowDecimals() {
		return allowDecimals;
	}

	public void setAllowDecimals(boolean allowDecimals) {
		this.allowDecimals = allowDecimals;
	}

}
