package com.uas.erp.model.upgrade;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的transferdetail
 * 
 * @author yingp
 *
 */
public class TransferDetail implements Saveable{

	private String td_fromtable;
	private String td_fromfield;
	private String td_tofield;
	
	private String td_trid;

	public String getTd_fromtable() {
		return td_fromtable;
	}

	public void setTd_fromtable(String td_fromtable) {
		this.td_fromtable = td_fromtable;
	}

	public String getTd_fromfield() {
		return td_fromfield;
	}

	public void setTd_fromfield(String td_fromfield) {
		this.td_fromfield = td_fromfield;
	}

	public String getTd_tofield() {
		return td_tofield;
	}

	public void setTd_tofield(String td_tofield) {
		this.td_tofield = td_tofield;
	}

	@JsonIgnore
	public String getTd_trid() {
		return td_trid;
	}

	public void setTd_trid(String td_trid) {
		this.td_trid = td_trid;
	}

	@Override
	public String table() {
		return "upgrade$transferdetail";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
