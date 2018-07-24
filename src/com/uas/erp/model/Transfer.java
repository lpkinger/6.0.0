package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 转单配置方案
 * 
 * @author yingp
 * 
 */
public class Transfer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tr_id;
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
	
	private List<Detail> details;

	public int getTr_id() {
		return tr_id;
	}

	public void setTr_id(int tr_id) {
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

	public String getTr_torowforkey() {
		return tr_torowforkey;
	}

	public String getTr_tocodecaller() {
		return tr_tocodecaller;
	}

	public void setTr_tocodecaller(String tr_tocodecaller) {
		this.tr_tocodecaller = tr_tocodecaller;
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

	public List<Detail> getDetails() {
		return details;
	}

	public void setDetails(List<Detail> details) {
		this.details = details;
	}

	/**
	 * 转单配置详细字段
	 * 
	 * @author yingp
	 * 
	 */
	public static class Detail implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int td_trid;
		private String td_fromtable;
		private String td_fromfield;
		private String td_tofield;

		public int getTd_trid() {
			return td_trid;
		}

		public void setTd_trid(int td_trid) {
			this.td_trid = td_trid;
		}

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
	}
}
