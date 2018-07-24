package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author yingp
 * 
 */
public class SysUpdates implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String plan_id;
	private Short version;
	private Date install_date;
	private String install_type;

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public Short getVersion() {
		return version;
	}

	public void setVersion(Short version) {
		this.version = version;
	}

	public Date getInstall_date() {
		return install_date;
	}

	public void setInstall_date(Date install_date) {
		this.install_date = install_date;
	}

	public String getInstall_type() {
		return install_type;
	}

	public void setInstall_type(String install_type) {
		this.install_type = install_type;
	}

}
