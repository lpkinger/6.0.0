package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的datalistdetail
 * 
 * @author yingp
 *
 */
public class DatalistDetail implements Saveable{

	private String dld_caller;
	private Float dld_detno;
	private String dld_field;
	private String dld_caption;
	private String dld_caption_en;
	private String dld_caption_fan;
	private Double dld_width;
	private String dld_fieldtype;
	private String dld_alignment;
	private String dld_table;
	private Short dld_editable;
	private String dld_render;
	private Short dld_flex;
	private Short dld_mobileused;

	private String dld_dlid;

	public String getDld_caller() {
		return dld_caller;
	}

	public void setDld_caller(String dld_caller) {
		this.dld_caller = dld_caller;
	}

	public Float getDld_detno() {
		return dld_detno;
	}

	public void setDld_detno(Float dld_detno) {
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

	public Double getDld_width() {
		return dld_width;
	}

	public void setDld_width(Double dld_width) {
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

	public Short getDld_editable() {
		return dld_editable;
	}

	public void setDld_editable(Short dld_editable) {
		this.dld_editable = dld_editable;
	}

	public String getDld_render() {
		return dld_render;
	}

	public void setDld_render(String dld_render) {
		this.dld_render = dld_render;
	}

	public Short getDld_flex() {
		return dld_flex;
	}

	public void setDld_flex(Short dld_flex) {
		this.dld_flex = dld_flex;
	}

	public Short getDld_mobileused() {
		return dld_mobileused;
	}

	public void setDld_mobileused(Short dld_mobileused) {
		this.dld_mobileused = dld_mobileused;
	}

	@JsonIgnore
	public String getDld_dlid() {
		return dld_dlid;
	}

	public void setDld_dlid(String dld_dlid) {
		this.dld_dlid = dld_dlid;
	}

	@Override
	public String table() {
		return "upgrade$datalistdetail";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
