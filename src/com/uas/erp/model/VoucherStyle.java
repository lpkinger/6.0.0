package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

public class VoucherStyle implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vs_code;
	private String vs_name;
	private String vs_pritable;
	private String vs_prikey1;
	private String vs_detailtable;
	private String vs_detkey1;
	private String vs_detkey2;
	private String vs_datalist;
	private String vs_datefield;
	private String vs_copfield;
	private String vs_inoutfield;
	private String vs_voucfield;
	private String vs_classfield;
	private String vs_explan;
	private String vs_datacondition;
	private String vs_updatecondition;
	private String vs_uncreatecondition;
	private Integer vs_id;
	private List<VoucherStyleDetail> details;

	public String getVs_code() {
		return vs_code;
	}

	public void setVs_code(String vs_code) {
		this.vs_code = vs_code;
	}

	public String getVs_name() {
		return vs_name;
	}

	public void setVs_name(String vs_name) {
		this.vs_name = vs_name;
	}

	public String getVs_pritable() {
		return vs_pritable;
	}

	public void setVs_pritable(String vs_pritable) {
		this.vs_pritable = vs_pritable;
	}

	public String getVs_prikey1() {
		return vs_prikey1;
	}

	public void setVs_prikey1(String vs_prikey1) {
		this.vs_prikey1 = vs_prikey1;
	}

	public String getVs_detailtable() {
		return vs_detailtable;
	}

	public void setVs_detailtable(String vs_detailtable) {
		this.vs_detailtable = vs_detailtable;
	}

	public String getVs_detkey1() {
		return vs_detkey1;
	}

	public void setVs_detkey1(String vs_detkey1) {
		this.vs_detkey1 = vs_detkey1;
	}

	public String getVs_detkey2() {
		return vs_detkey2;
	}

	public void setVs_detkey2(String vs_detkey2) {
		this.vs_detkey2 = vs_detkey2;
	}

	public String getVs_datalist() {
		return vs_datalist;
	}

	public void setVs_datalist(String vs_datalist) {
		this.vs_datalist = vs_datalist;
	}

	public String getVs_datefield() {
		return vs_datefield;
	}

	public void setVs_datefield(String vs_datefield) {
		this.vs_datefield = vs_datefield;
	}

	public String getVs_copfield() {
		return vs_copfield;
	}

	public void setVs_copfield(String vs_copfield) {
		this.vs_copfield = vs_copfield;
	}

	public String getVs_inoutfield() {
		return vs_inoutfield;
	}

	public void setVs_inoutfield(String vs_inoutfield) {
		this.vs_inoutfield = vs_inoutfield;
	}

	public String getVs_voucfield() {
		return vs_voucfield;
	}

	public void setVs_voucfield(String vs_voucfield) {
		this.vs_voucfield = vs_voucfield;
	}

	public String getVs_classfield() {
		return vs_classfield;
	}

	public void setVs_classfield(String vs_classfield) {
		this.vs_classfield = vs_classfield;
	}

	public String getVs_explan() {
		return vs_explan;
	}

	public void setVs_explan(String vs_explan) {
		this.vs_explan = vs_explan;
	}

	public String getVs_datacondition() {
		return vs_datacondition;
	}

	public void setVs_datacondition(String vs_datacondition) {
		this.vs_datacondition = vs_datacondition;
	}

	public String getVs_updatecondition() {
		return vs_updatecondition;
	}

	public void setVs_updatecondition(String vs_updatecondition) {
		this.vs_updatecondition = vs_updatecondition;
	}

	public String getVs_uncreatecondition() {
		return vs_uncreatecondition;
	}

	public void setVs_uncreatecondition(String vs_uncreatecondition) {
		this.vs_uncreatecondition = vs_uncreatecondition;
	}

	public Integer getVs_id() {
		return vs_id;
	}

	public void setVs_id(Integer vs_id) {
		this.vs_id = vs_id;
	}

	public List<VoucherStyleDetail> getDetails() {
		return details;
	}

	public void setDetails(List<VoucherStyleDetail> details) {
		this.details = details;
	}

	public static class VoucherStyleDetail {

		private String vd_code;
		private int vd_detno;
		private String vd_class;
		private String vd_catecode;
		private String vd_catedesc;
		private String vd_explanation;
		private String vd_sqlstr;
		private String vd_debitorcredit;
		private Integer vd_checkitem;
		private Integer vd_id;
		private String vd_asstable;
		private String vd_assrel;
		private Integer vd_vsid;
		private String vd_asstypef;
		private String vd_asscodef;
		private String vd_assnamef;

		public String getVd_code() {
			return vd_code;
		}

		public void setVd_code(String vd_code) {
			this.vd_code = vd_code;
		}

		public int getVd_detno() {
			return vd_detno;
		}

		public void setVd_detno(int vd_detno) {
			this.vd_detno = vd_detno;
		}

		public String getVd_class() {
			return vd_class;
		}

		public void setVd_class(String vd_class) {
			this.vd_class = vd_class;
		}

		public String getVd_catecode() {
			return vd_catecode;
		}

		public void setVd_catecode(String vd_catecode) {
			this.vd_catecode = vd_catecode;
		}

		public String getVd_catedesc() {
			return vd_catedesc;
		}

		public void setVd_catedesc(String vd_catedesc) {
			this.vd_catedesc = vd_catedesc;
		}

		public String getVd_explanation() {
			return vd_explanation;
		}

		public void setVd_explanation(String vd_explanation) {
			this.vd_explanation = vd_explanation;
		}

		public String getVd_sqlstr() {
			return vd_sqlstr;
		}

		public void setVd_sqlstr(String vd_sqlstr) {
			this.vd_sqlstr = vd_sqlstr;
		}

		public String getVd_debitorcredit() {
			return vd_debitorcredit;
		}

		public void setVd_debitorcredit(String vd_debitorcredit) {
			this.vd_debitorcredit = vd_debitorcredit;
		}

		public Integer getVd_checkitem() {
			return vd_checkitem;
		}

		public void setVd_checkitem(Integer vd_checkitem) {
			this.vd_checkitem = vd_checkitem;
		}

		public Integer getVd_id() {
			return vd_id;
		}

		public void setVd_id(Integer vd_id) {
			this.vd_id = vd_id;
		}

		public String getVd_asstable() {
			return vd_asstable;
		}

		public void setVd_asstable(String vd_asstable) {
			this.vd_asstable = vd_asstable;
		}

		public String getVd_assrel() {
			return vd_assrel;
		}

		public void setVd_assrel(String vd_assrel) {
			this.vd_assrel = vd_assrel;
		}

		public Integer getVd_vsid() {
			return vd_vsid;
		}

		public void setVd_vsid(Integer vd_vsid) {
			this.vd_vsid = vd_vsid;
		}

		public String getVd_asstypef() {
			return vd_asstypef;
		}

		public void setVd_asstypef(String vd_asstypef) {
			this.vd_asstypef = vd_asstypef;
		}

		public String getVd_asscodef() {
			return vd_asscodef;
		}

		public void setVd_asscodef(String vd_asscodef) {
			this.vd_asscodef = vd_asscodef;
		}

		public String getVd_assnamef() {
			return vd_assnamef;
		}

		public void setVd_assnamef(String vd_assnamef) {
			this.vd_assnamef = vd_assnamef;
		}

	}

}
