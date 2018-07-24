package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的documentsetup
 * 
 * @author yingp
 *
 */
public class DocumentSetup implements Saveable{

	private String ds_table;
	private String ds_code;
	private String ds_name;
	private Short ds_isautocode;
	private String ds_autocodecaller;
	private Short ds_isautoaudit;
	private Short ds_isautoprint;
	private String ds_dockind;
	private String ds_inorout;
	private Short ds_toar;
	private Short ds_toap;
	private Short ds_ismulti;
	
	private String plan_id;

	public String getDs_table() {
		return ds_table;
	}

	public void setDs_table(String ds_table) {
		this.ds_table = ds_table;
	}

	public String getDs_code() {
		return ds_code;
	}

	public void setDs_code(String ds_code) {
		this.ds_code = ds_code;
	}

	public String getDs_name() {
		return ds_name;
	}

	public void setDs_name(String ds_name) {
		this.ds_name = ds_name;
	}

	public Short getDs_isautocode() {
		return ds_isautocode;
	}

	public void setDs_isautocode(Short ds_isautocode) {
		this.ds_isautocode = ds_isautocode;
	}

	public String getDs_autocodecaller() {
		return ds_autocodecaller;
	}

	public void setDs_autocodecaller(String ds_autocodecaller) {
		this.ds_autocodecaller = ds_autocodecaller;
	}

	public Short getDs_isautoaudit() {
		return ds_isautoaudit;
	}

	public void setDs_isautoaudit(Short ds_isautoaudit) {
		this.ds_isautoaudit = ds_isautoaudit;
	}

	public Short getDs_isautoprint() {
		return ds_isautoprint;
	}

	public void setDs_isautoprint(Short ds_isautoprint) {
		this.ds_isautoprint = ds_isautoprint;
	}

	public String getDs_dockind() {
		return ds_dockind;
	}

	public void setDs_dockind(String ds_dockind) {
		this.ds_dockind = ds_dockind;
	}

	public String getDs_inorout() {
		return ds_inorout;
	}

	public void setDs_inorout(String ds_inorout) {
		this.ds_inorout = ds_inorout;
	}

	public Short getDs_toar() {
		return ds_toar;
	}

	public void setDs_toar(Short ds_toar) {
		this.ds_toar = ds_toar;
	}

	public Short getDs_toap() {
		return ds_toap;
	}

	public void setDs_toap(Short ds_toap) {
		this.ds_toap = ds_toap;
	}

	public Short getDs_ismulti() {
		return ds_ismulti;
	}

	public void setDs_ismulti(Short ds_ismulti) {
		this.ds_ismulti = ds_ismulti;
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
		return "upgrade$documentsetup";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
