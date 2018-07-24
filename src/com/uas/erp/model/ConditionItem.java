package com.uas.erp.model;

import java.io.Serializable;

public class ConditionItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String fieldLabel;
	private String id;
	private String xtype;
	private String type;
	private String value;
	private String relation;
	private String caption;
	private String html;
	private float columnWidth = 1;
	private int maxLength = 255;
	private String maxLengthText = "字数太长了哟";
	private int argnum;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getXtype() {
		return xtype;
	}

	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public float getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(float columnWidth) {
		this.columnWidth = columnWidth;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getMaxLengthText() {
		return maxLengthText;
	}

	public void setMaxLengthText(String maxLengthText) {
		this.maxLengthText = maxLengthText;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public int getArgnum() {
		return argnum;
	}

	public void setArgnum(int argnum) {
		this.argnum = argnum;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public ConditionItem() {

	}

	public ConditionItem(String title) {
		this.html = "<div onclick=\"javascript:collapse();\" style=\"background-color: #8DB6CD; margin-top:5px; margin-bottom:5px;height:16px \" title=\"收拢\"><h4>"
				+ title + "</h4></div>";
		this.columnWidth = 1;
		this.xtype = "label";
	}

}
