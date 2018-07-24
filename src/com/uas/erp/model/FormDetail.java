package com.uas.erp.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.Saveable;

public class FormDetail implements Saveable, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fd_id;
	private String fd_table;
	private String fd_caption;
	private String fd_field;
	private String fd_type;
	private String fd_allowblank;
	private String fd_readonly;
	private Float fd_columnwidth;
	private String fd_defaultvalue;
	private String fd_captionfan;
	private String fd_captionen;
	private String fd_dbfind;
	private int fd_detno;
	private Integer fd_fieldlength;
	private String fd_group;
	private String fd_logictype;
	private String fd_minvalue;
	private String fd_maxvalue;
	private Integer fd_foid;

	/**
	 * 需要校验
	 */
	private Float fd_check;
	private String fd_modify;
	/**
	 * formdetail render
	 * */
	private String fd_render;
	private String fd_func;

	public int getFd_id() {
		return fd_id;
	}

	public void setFd_id(int fd_id) {
		this.fd_id = fd_id;
	}

	public String getFd_table() {
		return fd_table;
	}

	public void setFd_table(String fd_table) {
		this.fd_table = fd_table;
	}

	public String getFd_caption() {
		return fd_caption;
	}

	public void setFd_caption(String fd_caption) {
		this.fd_caption = fd_caption;
	}

	public String getFd_field() {
		return fd_field;
	}

	public void setFd_field(String fd_field) {
		this.fd_field = fd_field;
	}

	public String getFd_type() {
		return fd_type;
	}

	public void setFd_type(String fd_type) {
		this.fd_type = fd_type;
	}

	public String getFd_allowblank() {
		return fd_allowblank;
	}

	public void setFd_allowblank(String fd_allowblank) {
		this.fd_allowblank = fd_allowblank;
	}

	public String getFd_readonly() {
		return fd_readonly;
	}

	public void setFd_readonly(String fd_readonly) {
		this.fd_readonly = fd_readonly;
	}

	public String getFd_defaultvalue() {
		return fd_defaultvalue;
	}

	public void setFd_defaultvalue(String fd_defaultvalue) {
		this.fd_defaultvalue = fd_defaultvalue;
	}

	public String getFd_captionfan() {
		return fd_captionfan;
	}

	public void setFd_captionfan(String fd_captionfan) {
		this.fd_captionfan = fd_captionfan;
	}

	public String getFd_captionen() {
		return fd_captionen;
	}

	public void setFd_captionen(String fd_captionen) {
		this.fd_captionen = fd_captionen;
	}

	public String getFd_dbfind() {
		return fd_dbfind;
	}

	public void setFd_dbfind(String fd_dbfind) {
		this.fd_dbfind = fd_dbfind;
	}

	public int getFd_detno() {
		return fd_detno;
	}

	public void setFd_detno(int fd_detno) {
		this.fd_detno = fd_detno;
	}

	public String getFd_logictype() {
		return fd_logictype;
	}

	public void setFd_logictype(String fd_logictype) {
		this.fd_logictype = fd_logictype;
	}

	public Float getFd_columnwidth() {
		return fd_columnwidth;
	}

	public void setFd_columnwidth(Float fd_columnwidth) {
		this.fd_columnwidth = fd_columnwidth;
	}

	public Integer getFd_fieldlength() {
		return fd_fieldlength;
	}

	public void setFd_fieldlength(Integer fd_fieldlength) {
		this.fd_fieldlength = fd_fieldlength;
	}

	public Float getFd_check() {
		return fd_check;
	}

	public void setFd_check(Float fd_check) {
		this.fd_check = fd_check;
	}

	public String getFd_group() {
		return fd_group;
	}

	public void setFd_group(String fd_group) {
		this.fd_group = fd_group;
	}

	@Override
	public String table() {
		return "formdetail";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "fd_id" };
	}

	/**
	 * 是否需要校验
	 * 
	 * @return
	 */
	public boolean isNeedCheck() {
		return this.fd_check != null && Constant.YES == this.fd_check;
	}

	public String getFd_minvalue() {
		return fd_minvalue;
	}

	public void setFd_minvalue(String fd_minvalue) {
		this.fd_minvalue = fd_minvalue;
	}

	public String getFd_modify() {
		return fd_modify;
	}

	public void setFd_modify(String fd_modify) {
		this.fd_modify = fd_modify;
	}

	public String getFd_render() {
		return fd_render;
	}

	public void setFd_render(String fd_render) {
		this.fd_render = fd_render;
	}

	public String getFd_func() {
		return fd_func;
	}

	public void setFd_func(String fd_func) {
		this.fd_func = fd_func;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@JsonIgnore
	@JSONField(serialize = false)
	public Integer getFd_foid() {
		return fd_foid;
	}

	public void setFd_foid(Integer fd_foid) {
		this.fd_foid = fd_foid;
	}

	public String getFd_maxvalue() {
		return fd_maxvalue;
	}

	public void setFd_maxvalue(String fd_maxvalue) {
		this.fd_maxvalue = fd_maxvalue;
	}

}
