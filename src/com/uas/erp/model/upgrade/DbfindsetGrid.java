package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的dbfindsetgrid
 * 
 * @author yingp
 *
 */
public class DbfindsetGrid implements Saveable{

	private String ds_caller;
	private Float ds_detno;
	private String ds_gridfield;
	private String ds_dbfindfield;
	private String ds_triggerfield;

	private String plan_id;

	public String getDs_caller() {
		return ds_caller;
	}

	public void setDs_caller(String ds_caller) {
		this.ds_caller = ds_caller;
	}

	public Float getDs_detno() {
		return ds_detno;
	}

	public void setDs_detno(Float ds_detno) {
		this.ds_detno = ds_detno;
	}

	public String getDs_gridfield() {
		return ds_gridfield;
	}

	public void setDs_gridfield(String ds_gridfield) {
		this.ds_gridfield = ds_gridfield;
	}

	public String getDs_dbfindfield() {
		return ds_dbfindfield;
	}

	public void setDs_dbfindfield(String ds_dbfindfield) {
		this.ds_dbfindfield = ds_dbfindfield;
	}

	public String getDs_triggerfield() {
		return ds_triggerfield;
	}

	public void setDs_triggerfield(String ds_triggerfield) {
		this.ds_triggerfield = ds_triggerfield;
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
		return "upgrade$dbfindsetgrid";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
