package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的poststylestep
 * 
 * @author yingp
 *
 */
public class PostStyleStep implements Saveable{

	private String pss_type;
	private String pss_sql;
	private String pss_remark;
	private String pss_othps;
	private Short pss_detno;
	
	private String pss_psid;

	public String getPss_type() {
		return pss_type;
	}

	public void setPss_type(String pss_type) {
		this.pss_type = pss_type;
	}

	public String getPss_sql() {
		return pss_sql;
	}

	public void setPss_sql(String pss_sql) {
		this.pss_sql = pss_sql;
	}

	public String getPss_remark() {
		return pss_remark;
	}

	public void setPss_remark(String pss_remark) {
		this.pss_remark = pss_remark;
	}

	public String getPss_othps() {
		return pss_othps;
	}

	public void setPss_othps(String pss_othps) {
		this.pss_othps = pss_othps;
	}

	public Short getPss_detno() {
		return pss_detno;
	}

	public void setPss_detno(Short pss_detno) {
		this.pss_detno = pss_detno;
	}

	@JsonIgnore
	public String getPss_psid() {
		return pss_psid;
	}

	public void setPss_psid(String pss_psid) {
		this.pss_psid = pss_psid;
	}

	@Override
	public String table() {
		return "upgrade$poststylestep";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
