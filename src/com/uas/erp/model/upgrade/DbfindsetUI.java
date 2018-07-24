package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的dbfindsetui
 * 
 * @author yingp
 *
 */
public class DbfindsetUI implements Saveable{

	private String ds_whichui;
	private String ds_whichdbfind;
	private String ds_likefield;
	private String ds_findtoui;
	private String ds_caption;
	private String ds_uifixedcondition;
	private String ds_dbcaption;
	private String ds_dbwidth;
	private String ds_enid;
	private String ds_caller;
	private String ds_orderby;
	private Short ds_allowreset;
	private String ds_tables;
	private Integer ds_autoheight;
	private String ds_error;

	private String plan_id;

	public String getDs_whichui() {
		return ds_whichui;
	}

	public void setDs_whichui(String ds_whichui) {
		this.ds_whichui = ds_whichui;
	}

	public String getDs_whichdbfind() {
		return ds_whichdbfind;
	}

	public void setDs_whichdbfind(String ds_whichdbfind) {
		this.ds_whichdbfind = ds_whichdbfind;
	}

	public String getDs_likefield() {
		return ds_likefield;
	}

	public void setDs_likefield(String ds_likefield) {
		this.ds_likefield = ds_likefield;
	}

	public String getDs_findtoui() {
		return ds_findtoui;
	}

	public void setDs_findtoui(String ds_findtoui) {
		this.ds_findtoui = ds_findtoui;
	}

	public String getDs_caption() {
		return ds_caption;
	}

	public void setDs_caption(String ds_caption) {
		this.ds_caption = ds_caption;
	}

	public String getDs_uifixedcondition() {
		return ds_uifixedcondition;
	}

	public void setDs_uifixedcondition(String ds_uifixedcondition) {
		this.ds_uifixedcondition = ds_uifixedcondition;
	}

	public String getDs_dbcaption() {
		return ds_dbcaption;
	}

	public void setDs_dbcaption(String ds_dbcaption) {
		this.ds_dbcaption = ds_dbcaption;
	}

	public String getDs_dbwidth() {
		return ds_dbwidth;
	}

	public void setDs_dbwidth(String ds_dbwidth) {
		this.ds_dbwidth = ds_dbwidth;
	}

	public String getDs_enid() {
		return ds_enid;
	}

	public void setDs_enid(String ds_enid) {
		this.ds_enid = ds_enid;
	}

	public String getDs_caller() {
		return ds_caller;
	}

	public void setDs_caller(String ds_caller) {
		this.ds_caller = ds_caller;
	}

	public String getDs_orderby() {
		return ds_orderby;
	}

	public void setDs_orderby(String ds_orderby) {
		this.ds_orderby = ds_orderby;
	}

	public Short getDs_allowreset() {
		return ds_allowreset;
	}

	public void setDs_allowreset(Short ds_allowreset) {
		this.ds_allowreset = ds_allowreset;
	}

	public String getDs_tables() {
		return ds_tables;
	}

	public void setDs_tables(String ds_tables) {
		this.ds_tables = ds_tables;
	}

	public Integer getDs_autoheight() {
		return ds_autoheight;
	}

	public void setDs_autoheight(Integer ds_autoheight) {
		this.ds_autoheight = ds_autoheight;
	}

	public String getDs_error() {
		return ds_error;
	}

	public void setDs_error(String ds_error) {
		this.ds_error = ds_error;
	}

	@JsonIgnore
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	@Override
	public String table() {
		return "upgrade$dbfindsetui";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
