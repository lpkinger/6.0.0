package com.uas.erp.model;

import java.io.Serializable;

public class Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ca_id;
	private String ca_code;
	private String ca_name;
	private String ca_englishname;
	private String ca_codename;
	private String ca_description;
	private Integer ca_subof;
	private Integer ca_level;
	private Integer ca_type;
	private String ca_typename;
	private String ca_pagetype;
	private String ca_class;
	private String ca_helpcode;
	private Integer ca_defaultset;
	private Integer ca_currencytype;
	private String ca_currency;
	private Integer ca_used;
	private Integer ca_emid;
	private String ca_remark;
	private String ca_asstype;
	private String ca_assname;
	private Integer ca_checkrate;
	private Integer ca_cashflow;
	private Integer ca_cashier;
	private Integer ca_isleaf;
	private String ca_bank;
	private String ca_bankaccount;
	private Integer ca_iscash;
	private Integer ca_isbank;
	private Integer ca_iscashbank;
	private Float ca_nowbalance;
	private Float ca_nowbalance2;
	private String ca_cop;
	private String CURRENTMASTER;
	private Float ca_currencyrate;

	public Float getCa_currencyrate() {
		return ca_currencyrate;
	}

	public void setCa_currencyrate(Float ca_currencyrate) {
		this.ca_currencyrate = ca_currencyrate;
	}

	public int getCa_id() {
		return ca_id;
	}

	public void setCa_id(int ca_id) {
		this.ca_id = ca_id;
	}

	public String getCa_code() {
		return ca_code;
	}

	public void setCa_code(String ca_code) {
		this.ca_code = ca_code;
	}

	public String getCa_name() {
		return ca_name;
	}

	public void setCa_name(String ca_name) {
		this.ca_name = ca_name;
	}

	public String getCa_englishname() {
		return ca_englishname;
	}

	public void setCa_englishname(String ca_englishname) {
		this.ca_englishname = ca_englishname;
	}

	public String getCa_codename() {
		return ca_codename;
	}

	public void setCa_codename(String ca_codename) {
		this.ca_codename = ca_codename;
	}

	public String getCa_description() {
		return ca_description;
	}

	public void setCa_description(String ca_description) {
		this.ca_description = ca_description;
	}

	public Integer getCa_subof() {
		return ca_subof;
	}

	public void setCa_subof(Integer ca_subof) {
		this.ca_subof = ca_subof;
	}

	public Integer getCa_level() {
		return ca_level;
	}

	public void setCa_level(Integer ca_level) {
		this.ca_level = ca_level;
	}

	public Integer getCa_type() {
		return ca_type;
	}

	public void setCa_type(Integer ca_type) {
		this.ca_type = ca_type;
	}

	public String getCa_typename() {
		return ca_typename;
	}

	public void setCa_typename(String ca_typename) {
		this.ca_typename = ca_typename;
	}

	public String getCa_pagetype() {
		return ca_pagetype;
	}

	public void setCa_pagetype(String ca_pagetype) {
		this.ca_pagetype = ca_pagetype;
	}

	public String getCa_class() {
		return ca_class;
	}

	public void setCa_class(String ca_class) {
		this.ca_class = ca_class;
	}

	public String getCa_helpcode() {
		return ca_helpcode;
	}

	public void setCa_helpcode(String ca_helpcode) {
		this.ca_helpcode = ca_helpcode;
	}

	public Integer getCa_defaultset() {
		return ca_defaultset;
	}

	public void setCa_defaultset(Integer ca_defaultset) {
		this.ca_defaultset = ca_defaultset;
	}

	public Integer getCa_currencytype() {
		return ca_currencytype;
	}

	public void setCa_currencytype(Integer ca_currencytype) {
		this.ca_currencytype = ca_currencytype;
	}

	public String getCa_currency() {
		return ca_currency;
	}

	public void setCa_currency(String ca_currency) {
		this.ca_currency = ca_currency;
	}

	public Integer getCa_used() {
		return ca_used;
	}

	public void setCa_used(Integer ca_used) {
		this.ca_used = ca_used;
	}

	public Integer getCa_emid() {
		return ca_emid;
	}

	public void setCa_emid(Integer ca_emid) {
		this.ca_emid = ca_emid;
	}

	public String getCa_remark() {
		return ca_remark;
	}

	public void setCa_remark(String ca_remark) {
		this.ca_remark = ca_remark;
	}

	public String getCa_asstype() {
		return ca_asstype;
	}

	public void setCa_asstype(String ca_asstype) {
		this.ca_asstype = ca_asstype;
	}

	public String getCa_assname() {
		return ca_assname;
	}

	public void setCa_assname(String ca_assname) {
		this.ca_assname = ca_assname;
	}

	public Integer getCa_checkrate() {
		return ca_checkrate;
	}

	public void setCa_checkrate(Integer ca_checkrate) {
		this.ca_checkrate = ca_checkrate;
	}

	public Integer getCa_cashflow() {
		return ca_cashflow;
	}

	public void setCa_cashflow(Integer ca_cashflow) {
		this.ca_cashflow = ca_cashflow;
	}

	public Integer getCa_cashier() {
		return ca_cashier;
	}

	public void setCa_cashier(Integer ca_cashier) {
		this.ca_cashier = ca_cashier;
	}

	public Integer getCa_isleaf() {
		return ca_isleaf;
	}

	public void setCa_isleaf(Integer ca_isleaf) {
		this.ca_isleaf = ca_isleaf;
	}

	public String getCa_bank() {
		return ca_bank;
	}

	public void setCa_bank(String ca_bank) {
		this.ca_bank = ca_bank;
	}

	public String getCa_bankaccount() {
		return ca_bankaccount;
	}

	public void setCa_bankaccount(String ca_bankaccount) {
		this.ca_bankaccount = ca_bankaccount;
	}

	public Integer getCa_iscash() {
		return ca_iscash;
	}

	public void setCa_iscash(Integer ca_iscash) {
		this.ca_iscash = ca_iscash;
	}

	public Integer getCa_isbank() {
		return ca_isbank;
	}

	public void setCa_isbank(Integer ca_isbank) {
		this.ca_isbank = ca_isbank;
	}

	public Integer getCa_iscashbank() {
		return ca_iscashbank;
	}

	public void setCa_iscashbank(Integer ca_iscashbank) {
		this.ca_iscashbank = ca_iscashbank;
	}

	public Float getCa_nowbalance() {
		return ca_nowbalance;
	}

	public void setCa_nowbalance(Float ca_nowbalance) {
		this.ca_nowbalance = ca_nowbalance;
	}

	public Float getCa_nowbalance2() {
		return ca_nowbalance2;
	}

	public void setCa_nowbalance2(Float ca_nowbalance2) {
		this.ca_nowbalance2 = ca_nowbalance2;
	}

	public String getCa_cop() {
		return ca_cop;
	}

	public void setCa_cop(String ca_cop) {
		this.ca_cop = ca_cop;
	}

	public String getCURRENTMASTER() {
		return CURRENTMASTER;
	}

	public void setCURRENTMASTER(String cURRENTMASTER) {
		CURRENTMASTER = cURRENTMASTER;
	}

}
