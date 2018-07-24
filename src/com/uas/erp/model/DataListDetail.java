package com.uas.erp.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.erp.dao.Saveable;

/**
 * 
 *
 */
public class DataListDetail implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dld_id;
	private String dld_caller;
	private int dld_detno;
	private String dld_field;
	private String dld_caption;
	private String dld_caption_en;
	private String dld_caption_fan;
	private Integer dld_width;
	private String dld_fieldtype;
	private String dld_alignment;
	private String dld_table;
	private DataList dataList;
	private Integer dld_editable;
	private String dld_render;
	private Float dld_flex;
	private String dld_summarytype;
	private Integer dde_width;
	private Integer dde_detno;
	private String dde_orderby;
	private String dde_priority;
	private Integer dld_dlid;

	public DataList getDataList() {
		return dataList;
	}

	public void setDataList(DataList dataList) {
		this.dataList = dataList;
	}

	public Integer getDld_editable() {
		return dld_editable;
	}

	public void setDld_editable(Integer dld_editable) {
		this.dld_editable = dld_editable;
	}

	public String getDld_caption_en() {
		return dld_caption_en;
	}

	public void setDld_caption_en(String dld_caption_en) {
		this.dld_caption_en = dld_caption_en;
	}

	public String getDld_caption_fan() {
		return dld_caption_fan;
	}

	public void setDld_caption_fan(String dld_caption_fan) {
		this.dld_caption_fan = dld_caption_fan;
	}

	public int getDld_id() {
		return dld_id;
	}

	public void setDld_id(int dld_id) {
		this.dld_id = dld_id;
	}

	public String getDld_caller() {
		return dld_caller;
	}

	public void setDld_caller(String dld_caller) {
		this.dld_caller = dld_caller;
	}

	public int getDld_detno() {
		return dld_detno;
	}

	public void setDld_detno(int dld_detno) {
		this.dld_detno = dld_detno;
	}

	public String getDld_field() {
		return dld_field;
	}

	public void setDld_field(String dld_field) {
		this.dld_field = dld_field;
	}

	public String getDld_caption() {
		return dld_caption;
	}

	public void setDld_caption(String dld_caption) {
		this.dld_caption = dld_caption;
	}

	public Integer getDld_width() {
		return dld_width;
	}

	public void setDld_width(Integer dld_width) {
		this.dld_width = dld_width;
	}

	public String getDld_fieldtype() {
		return dld_fieldtype;
	}

	public void setDld_fieldtype(String dld_fieldtype) {
		this.dld_fieldtype = dld_fieldtype;
	}

	public String getDld_alignment() {
		return dld_alignment;
	}

	public void setDld_alignment(String dld_alignment) {
		this.dld_alignment = dld_alignment;
	}

	public String getDld_table() {
		return dld_table;
	}

	public void setDld_table(String dld_table) {
		this.dld_table = dld_table;
	}

	public String getDld_render() {
		return dld_render;
	}

	public void setDld_render(String dld_render) {
		this.dld_render = dld_render;
	}

	public Float getDld_flex() {
		return dld_flex;
	}

	public void setDld_flex(Float dld_flex) {
		this.dld_flex = dld_flex;
	}

	public String getDld_summarytype() {
		return dld_summarytype;
	}

	public void setDld_summarytype(String dld_summarytype) {
		this.dld_summarytype = dld_summarytype;
	}

	public Integer getDde_width() {
		return dde_width;
	}

	public void setDde_width(Integer dde_width) {
		this.dde_width = dde_width;
		if (dde_width != null)
			this.dld_width = dde_width;
	}

	public Integer getDde_detno() {
		return dde_detno;
	}

	public void setDde_detno(Integer dde_detno) {
		this.dde_detno = dde_detno;
		if (dde_detno != null)
			this.dld_detno = dde_detno;
	}

	public String getDde_orderby() {
		return dde_orderby;
	}

	public void setDde_orderby(String dde_orderby) {
		this.dde_orderby = dde_orderby;
	}

	public String getDde_priority() {
		return dde_priority;
	}

	public void setDde_priority(String dde_priority) {
		this.dde_priority = dde_priority;
	}

	@Override
	public String table() {
		return "datalistdetail";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "dld_id" };
	}

	@JsonIgnore
	@JSONField(serialize = false)
	public Integer getDld_dlid() {
		return dld_dlid;
	}

	public void setDld_dlid(Integer dld_dlid) {
		this.dld_dlid = dld_dlid;
	}

}
