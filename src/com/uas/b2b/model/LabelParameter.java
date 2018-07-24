package com.uas.b2b.model;


import com.uas.erp.core.support.KeyEntity;

public class LabelParameter extends KeyEntity{
	
	private Long lp_id;
	private Long lp_laid;
	private String lp_detno;
	private String lp_name;
	private String lp_valuetype;
	private String lp_leftrate;
	private String lp_toprate;
	private String lp_font;
	private String lp_encode;
	private String lp_size;
	private String lp_ifshownote;
	private String lp_height;
	private String lp_width;
	private String lp_notealignjustify;
	private String lp_sendstatus;

	public Long getLp_id() {
		return lp_id;
	}

	public void setLp_id(Long lp_id) {
		this.lp_id = lp_id;
	}

	public Long getLp_laid() {
		return lp_laid;
	}

	public void setLp_laid(Long lp_laid) {
		this.lp_laid = lp_laid;
	}

	public String getLp_detno() {
		return lp_detno;
	}

	public void setLp_detno(String lp_detno) {
		this.lp_detno = lp_detno;
	}

	public String getLp_name() {
		return lp_name;
	}

	public void setLp_name(String lp_name) {
		this.lp_name = lp_name;
	}

	public String getLp_valuetype() {
		return lp_valuetype;
	}

	public void setLp_valuetype(String lp_valuetype) {
		this.lp_valuetype = lp_valuetype;
	}

	public String getLp_leftrate() {
		return lp_leftrate;
	}

	public void setLp_leftrate(String lp_leftrate) {
		this.lp_leftrate = lp_leftrate;
	}

	public String getLp_toprate() {
		return lp_toprate;
	}

	public void setLp_toprate(String lp_toprate) {
		this.lp_toprate = lp_toprate;
	}

	public String getLp_font() {
		return lp_font;
	}

	public void setLp_font(String lp_font) {
		this.lp_font = lp_font;
	}

	public String getLp_encode() {
		return lp_encode;
	}

	public void setLp_encode(String lp_encode) {
		this.lp_encode = lp_encode;
	}

	public String getLp_size() {
		return lp_size;
	}

	public void setLp_size(String lp_size) {
		this.lp_size = lp_size;
	}

	public String getLp_ifshownote() {
		return lp_ifshownote;
	}

	public void setLp_ifshownote(String lp_ifshownote) {
		this.lp_ifshownote = lp_ifshownote;
	}

	public String getLp_height() {
		return lp_height;
	}

	public void setLp_height(String lp_height) {
		this.lp_height = lp_height;
	}

	public String getLp_width() {
		return lp_width;
	}

	public void setLp_width(String lp_width) {
		this.lp_width = lp_width;
	}

	public String getLp_notealignjustify() {
		return lp_notealignjustify;
	}

	public void setLp_notealignjustify(String lp_notealignjustify) {
		this.lp_notealignjustify = lp_notealignjustify;
	}

	public String getLp_sendstatus() {
		return lp_sendstatus;
	}

	public void setLp_sendstatus(String lp_sendstatus) {
		this.lp_sendstatus = lp_sendstatus;
	}

	@Override
	public Object getKey() {
		return this.lp_id;
	}

}
