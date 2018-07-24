package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的poststyledetail
 * 
 * @author yingp
 *
 */
public class PostStyleDetail implements Saveable{

	private String psd_fromto;
	private String psd_field;
	private String psd_value;
	
	private String plan_id;

	public String getPsd_fromto() {
		return psd_fromto;
	}

	public void setPsd_fromto(String psd_fromto) {
		this.psd_fromto = psd_fromto;
	}

	public String getPsd_field() {
		return psd_field;
	}

	public void setPsd_field(String psd_field) {
		this.psd_field = psd_field;
	}

	public String getPsd_value() {
		return psd_value;
	}

	public void setPsd_value(String psd_value) {
		this.psd_value = psd_value;
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
		return "upgrade$poststyledetail";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
