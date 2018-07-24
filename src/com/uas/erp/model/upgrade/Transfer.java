package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的transfers
 * 
 * @author yingp
 *
 */
public class Transfer implements Saveable{

	private String tr_id;
	private String tr_title;
	private String tr_caller;
	private String tr_pagecaller;
	private String tr_fromtable;
	private String tr_fromtabledet;
	private String tr_fromtablesql;
	private String tr_fromdockey;
	private String tr_fromrowkey;
	private String tr_fromrownum;
	private String tr_fromrowqty;
	private String tr_fromrowyqty;
	private String tr_totable;
	private String tr_torowkey;
	private String tr_torownum;
	private String tr_tocodekey;
	private String tr_tocodecaller;
	private String tr_torowforkey;
	private String tr_mode;

	private List<TransferDetail> details;
	
	private String plan_id;

	@JsonIgnore
	public String getTr_id() {
		return tr_id;
	}

	public void setTr_id(String tr_id) {
		this.tr_id = tr_id;
	}

	public String getTr_title() {
		return tr_title;
	}

	public void setTr_title(String tr_title) {
		this.tr_title = tr_title;
	}

	public String getTr_caller() {
		return tr_caller;
	}

	public void setTr_caller(String tr_caller) {
		this.tr_caller = tr_caller;
	}

	public String getTr_pagecaller() {
		return tr_pagecaller;
	}

	public void setTr_pagecaller(String tr_pagecaller) {
		this.tr_pagecaller = tr_pagecaller;
	}

	public String getTr_fromtable() {
		return tr_fromtable;
	}

	public void setTr_fromtable(String tr_fromtable) {
		this.tr_fromtable = tr_fromtable;
	}

	public String getTr_fromtabledet() {
		return tr_fromtabledet;
	}

	public void setTr_fromtabledet(String tr_fromtabledet) {
		this.tr_fromtabledet = tr_fromtabledet;
	}

	public String getTr_fromtablesql() {
		return tr_fromtablesql;
	}

	public void setTr_fromtablesql(String tr_fromtablesql) {
		this.tr_fromtablesql = tr_fromtablesql;
	}

	public String getTr_fromdockey() {
		return tr_fromdockey;
	}

	public void setTr_fromdockey(String tr_fromdockey) {
		this.tr_fromdockey = tr_fromdockey;
	}

	public String getTr_fromrowkey() {
		return tr_fromrowkey;
	}

	public void setTr_fromrowkey(String tr_fromrowkey) {
		this.tr_fromrowkey = tr_fromrowkey;
	}

	public String getTr_fromrownum() {
		return tr_fromrownum;
	}

	public void setTr_fromrownum(String tr_fromrownum) {
		this.tr_fromrownum = tr_fromrownum;
	}

	public String getTr_fromrowqty() {
		return tr_fromrowqty;
	}

	public void setTr_fromrowqty(String tr_fromrowqty) {
		this.tr_fromrowqty = tr_fromrowqty;
	}

	public String getTr_fromrowyqty() {
		return tr_fromrowyqty;
	}

	public void setTr_fromrowyqty(String tr_fromrowyqty) {
		this.tr_fromrowyqty = tr_fromrowyqty;
	}

	public String getTr_totable() {
		return tr_totable;
	}

	public void setTr_totable(String tr_totable) {
		this.tr_totable = tr_totable;
	}

	public String getTr_torowkey() {
		return tr_torowkey;
	}

	public void setTr_torowkey(String tr_torowkey) {
		this.tr_torowkey = tr_torowkey;
	}

	public String getTr_torownum() {
		return tr_torownum;
	}

	public void setTr_torownum(String tr_torownum) {
		this.tr_torownum = tr_torownum;
	}

	public String getTr_tocodekey() {
		return tr_tocodekey;
	}

	public void setTr_tocodekey(String tr_tocodekey) {
		this.tr_tocodekey = tr_tocodekey;
	}

	public String getTr_tocodecaller() {
		return tr_tocodecaller;
	}

	public void setTr_tocodecaller(String tr_tocodecaller) {
		this.tr_tocodecaller = tr_tocodecaller;
	}

	public String getTr_torowforkey() {
		return tr_torowforkey;
	}

	public void setTr_torowforkey(String tr_torowforkey) {
		this.tr_torowforkey = tr_torowforkey;
	}

	public String getTr_mode() {
		return tr_mode;
	}

	public void setTr_mode(String tr_mode) {
		this.tr_mode = tr_mode;
	}

	public List<TransferDetail> getDetails() {
		return details;
	}

	public void setDetails(List<TransferDetail> details) {
		this.details = details;
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
		return "upgrade$transfers";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
