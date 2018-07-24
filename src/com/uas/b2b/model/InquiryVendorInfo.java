package com.uas.b2b.model;

import com.uas.erp.core.support.KeyEntity;

public class InquiryVendorInfo extends KeyEntity{

	/**
	 * 当前账套企业名称
	 */
	private String en_name;
	
	/**
	 * 企业营业执照号
	 */
	private String en_businesscode;
	
	/**
	 * 管理员姓名
	 */
	private String en_adminname;
	
	/**
	 * 管理员UU号
	 */
	private String en_adminuu;
	
	/**
	 * 供应商编号
	 */
	private String ve_code;
	
	/**
	 * 供应商名称
	 */
	private String ve_name;
	
	/**
	 * 供应商联系邮箱
	 */
	private String ve_email;
	
	/**
	 * 供应商联系人
	 */
	private String ve_contact;
	
	/**
	 * 供应商联系电话
	 */
	private String ve_mobile;
	
	/**
	 * 供应商营业执照
	 */
	private String ve_webserver;
	
	/**
	 * 供应商UU
	 */
	private String ve_uu;
	
	/**
	 * 平台供应商关系表的id，方便传回进行更新状态
	 * 
	 */
	private Long b2b_id;

	public String getEn_name() {
		return en_name;
	}

	public void setEn_name(String en_name) {
		this.en_name = en_name;
	}

	public String getEn_businesscode() {
		return en_businesscode;
	}

	public void setEn_businesscode(String en_businesscode) {
		this.en_businesscode = en_businesscode;
	}

	public String getEn_adminname() {
		return en_adminname;
	}

	public void setEn_adminname(String en_adminname) {
		this.en_adminname = en_adminname;
	}

	public String getEn_adminuu() {
		return en_adminuu;
	}

	public void setEn_adminuu(String en_adminuu) {
		this.en_adminuu = en_adminuu;
	}

	public String getVe_code() {
		return ve_code;
	}

	public void setVe_code(String ve_code) {
		this.ve_code = ve_code;
	}

	public String getVe_name() {
		return ve_name;
	}

	public void setVe_name(String ve_name) {
		this.ve_name = ve_name;
	}

	public String getVe_email() {
		return ve_email;
	}

	public void setVe_email(String ve_email) {
		this.ve_email = ve_email;
	}

	public String getVe_contact() {
		return ve_contact;
	}

	public void setVe_contact(String ve_contact) {
		this.ve_contact = ve_contact;
	}

	public String getVe_mobile() {
		return ve_mobile;
	}

	public void setVe_mobile(String ve_mobile) {
		this.ve_mobile = ve_mobile;
	}

	public String getVe_webserver() {
		return ve_webserver;
	}

	public void setVe_webserver(String ve_webserver) {
		this.ve_webserver = ve_webserver;
	}

	public String getVe_uu() {
		return ve_uu;
	}

	public void setVe_uu(String ve_uu) {
		this.ve_uu = ve_uu;
	}

	public Long getB2b_id() {
		return b2b_id;
	}

	public void setB2b_id(Long b2b_id) {
		this.b2b_id = b2b_id;
	}

	@Override
	public Object getKey() {
		return this.b2b_id;
	}
	
}
