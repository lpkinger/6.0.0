package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的dbfindsetdetail
 * 
 * @author yingp
 *
 */
public class DbfindsetDetail implements Saveable{

	private String dd_caller;
	private Float dd_ddno;
	private String dd_fieldname;
	private String dd_fieldcaption;
	private Short dd_fieldalignment;
	private String dd_fieldformat;
	private Double dd_fieldwidth;
	private Short dd_fieldinvisible;
	private String dd_table;
	private String dd_fieldtype;
	private String dd_fieldcaption_fan;
	private String dd_fieldcaption_en;
	private Short dd_editable;
	private String dd_render;

	private String dd_dsid;

	public String getDd_caller() {
		return dd_caller;
	}

	public void setDd_caller(String dd_caller) {
		this.dd_caller = dd_caller;
	}

	public Float getDd_ddno() {
		return dd_ddno;
	}

	public void setDd_ddno(Float dd_ddno) {
		this.dd_ddno = dd_ddno;
	}

	public String getDd_fieldname() {
		return dd_fieldname;
	}

	public void setDd_fieldname(String dd_fieldname) {
		this.dd_fieldname = dd_fieldname;
	}

	public String getDd_fieldcaption() {
		return dd_fieldcaption;
	}

	public void setDd_fieldcaption(String dd_fieldcaption) {
		this.dd_fieldcaption = dd_fieldcaption;
	}

	public Short getDd_fieldalignment() {
		return dd_fieldalignment;
	}

	public void setDd_fieldalignment(Short dd_fieldalignment) {
		this.dd_fieldalignment = dd_fieldalignment;
	}

	public String getDd_fieldformat() {
		return dd_fieldformat;
	}

	public void setDd_fieldformat(String dd_fieldformat) {
		this.dd_fieldformat = dd_fieldformat;
	}

	public Double getDd_fieldwidth() {
		return dd_fieldwidth;
	}

	public void setDd_fieldwidth(Double dd_fieldwidth) {
		this.dd_fieldwidth = dd_fieldwidth;
	}

	public Short getDd_fieldinvisible() {
		return dd_fieldinvisible;
	}

	public void setDd_fieldinvisible(Short dd_fieldinvisible) {
		this.dd_fieldinvisible = dd_fieldinvisible;
	}

	public String getDd_table() {
		return dd_table;
	}

	public void setDd_table(String dd_table) {
		this.dd_table = dd_table;
	}

	public String getDd_fieldtype() {
		return dd_fieldtype;
	}

	public void setDd_fieldtype(String dd_fieldtype) {
		this.dd_fieldtype = dd_fieldtype;
	}

	public String getDd_fieldcaption_fan() {
		return dd_fieldcaption_fan;
	}

	public void setDd_fieldcaption_fan(String dd_fieldcaption_fan) {
		this.dd_fieldcaption_fan = dd_fieldcaption_fan;
	}

	public String getDd_fieldcaption_en() {
		return dd_fieldcaption_en;
	}

	public void setDd_fieldcaption_en(String dd_fieldcaption_en) {
		this.dd_fieldcaption_en = dd_fieldcaption_en;
	}

	public Short getDd_editable() {
		return dd_editable;
	}

	public void setDd_editable(Short dd_editable) {
		this.dd_editable = dd_editable;
	}

	public String getDd_render() {
		return dd_render;
	}

	public void setDd_render(String dd_render) {
		this.dd_render = dd_render;
	}

	@JsonIgnore
	public String getDd_dsid() {
		return dd_dsid;
	}

	public void setDd_dsid(String dd_dsid) {
		this.dd_dsid = dd_dsid;
	}

	@Override
	public String table() {
		return "upgrade$dbfindsetdetail";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
