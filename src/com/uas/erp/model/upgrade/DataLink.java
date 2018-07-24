package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的datalink
 * 
 * @author yingp
 *
 */
public class DataLink implements Saveable{

	private String dl_title;
	private String dl_tablename;
	private String dl_fieldname;
	private String dl_link;
	private String dl_tokentab1;
	private String dl_tokencol1;
	private String dl_tokentab2;
	private String dl_tokencol2;
	
	private String plan_id;

	public String getDl_title() {
		return dl_title;
	}

	public void setDl_title(String dl_title) {
		this.dl_title = dl_title;
	}

	public String getDl_tablename() {
		return dl_tablename;
	}

	public void setDl_tablename(String dl_tablename) {
		this.dl_tablename = dl_tablename;
	}

	public String getDl_fieldname() {
		return dl_fieldname;
	}

	public void setDl_fieldname(String dl_fieldname) {
		this.dl_fieldname = dl_fieldname;
	}

	public String getDl_link() {
		return dl_link;
	}

	public void setDl_link(String dl_link) {
		this.dl_link = dl_link;
	}

	public String getDl_tokentab1() {
		return dl_tokentab1;
	}

	public void setDl_tokentab1(String dl_tokentab1) {
		this.dl_tokentab1 = dl_tokentab1;
	}

	public String getDl_tokencol1() {
		return dl_tokencol1;
	}

	public void setDl_tokencol1(String dl_tokencol1) {
		this.dl_tokencol1 = dl_tokencol1;
	}

	public String getDl_tokentab2() {
		return dl_tokentab2;
	}

	public void setDl_tokentab2(String dl_tokentab2) {
		this.dl_tokentab2 = dl_tokentab2;
	}

	public String getDl_tokencol2() {
		return dl_tokencol2;
	}

	public void setDl_tokencol2(String dl_tokencol2) {
		this.dl_tokencol2 = dl_tokencol2;
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
		return "upgrade$datalink";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
