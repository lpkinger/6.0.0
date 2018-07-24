package com.uas.erp.model;

import java.io.Serializable;

public class DataStoreDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dsd_id;
	private int dsd_mainid;
	private String dsd_caption;
	private String dsd_field;
	private String dsd_conditionisneed;
	private String dsd_conditiontype;
	private String dsd_fieldtype;

	public int getDsd_id() {
		return dsd_id;
	}

	public void setDsd_id(int dsd_id) {
		this.dsd_id = dsd_id;
	}

	public int getDsd_mainid() {
		return dsd_mainid;
	}

	public void setDsd_mainid(int dsd_mainid) {
		this.dsd_mainid = dsd_mainid;
	}

	public String getDsd_caption() {
		return dsd_caption;
	}

	public void setDsd_caption(String dsd_caption) {
		this.dsd_caption = dsd_caption;
	}

	public String getDsd_field() {
		return dsd_field;
	}

	public void setDsd_field(String dsd_field) {
		this.dsd_field = dsd_field;
	}

	public String getDsd_conditionisneed() {
		return dsd_conditionisneed;
	}

	public void setDsd_conditionisneed(String dsd_conditionisneed) {
		this.dsd_conditionisneed = dsd_conditionisneed;
	}

	public String getDsd_conditiontype() {
		return dsd_conditiontype;
	}

	public void setDsd_conditiontype(String dsd_conditiontype) {
		this.dsd_conditiontype = dsd_conditiontype;
	}

	public String getDsd_fieldtype() {
		return dsd_fieldtype;
	}

	public void setDsd_fieldtype(String dsd_fieldtype) {
		this.dsd_fieldtype = dsd_fieldtype;
	}

	public ConditionItem getConditionItem() {
		String fieldtype = this.dsd_fieldtype;
		ConditionItem item = new ConditionItem();
		item.setColumnWidth((float) 0.33);
		if (fieldtype.contains("varchar2")) {
			item.setType("textfield");

			item.setMaxLength(Integer.parseInt(fieldtype.split("[(]")[1].split("[)]")[0]));
		}
		if (fieldtype.contains("int")) {
			item.setType("numberfield");
		} else if (fieldtype.contains("date")) {
			item.setType("datefield");
		} else {
			item.setType("textfield");

		}
		item.setXtype("dyconfield");
		item.setCaption(this.dsd_caption);
		item.setId(this.dsd_field);
		item.setName(this.dsd_field);
		item.setRelation(this.dsd_conditiontype);
		return item;
	}
}
