package com.uas.b2b.model;

/**
 * erp的vendor信息
 * 
 * @author hejq
 * @time 创建时间：2017年6月7日
 */
public class Vendor {

	/**
	 * id
	 */
	private Integer ve_id;

	/**
	 * 供应商编号
	 */
	private String ve_code;

	/**
	 * 供应商名称
	 */
	private String ve_name;

	/**
	 * 供应商地址
	 */
	private String ve_add1;

	/**
	 * 供应商联系人
	 */
	private String ve_contact;

	/**
	 * 供应商电话
	 */
	private String ve_tel;

	/**
	 * 供应商简称
	 */
	private String ve_shortname;

	/**
	 * 是否启用b2b
	 */
	private Short ve_b2benable;

	/**
	 * 营业执照号
	 */
	private String ve_webserver;
	/**
	 * 法定代表人
	 */
	private String ve_legalman;
	/**
	 * 邮箱
	 */
	private String ve_email;
	/**
	 * 供应商uu
	 */
	private String ve_uu;
	/**
	 * 联系人手机
	 */
	private String ve_mobile;
	/**
	 * 经营范围
	 * @return
	 */
	private String ve_businessrange;
	/**
	 * 行业
	 * @return
	 */
	private String ve_industry;

	public Short getVendorSwitch() {
		return vendorSwitch;
	}

	public void setVendorSwitch(Short vendorSwitch) {
		this.vendorSwitch = vendorSwitch;
	}

	public Short getServicerSwitch() {
		return servicerSwitch;
	}

	public void setServicerSwitch(Short servicerSwitch) {
		this.servicerSwitch = servicerSwitch;
	}

	/**
	 * is b2b 是：1，否：0
	 */
	private int b2b;
	
	private Long b2b_vendor_id;
	/**
	 * @return
	 */
	private Short vendorSwitch;
	private Short servicerSwitch;
	public Integer getVe_id() {
		return ve_id;
	}

	public void setVe_id(Integer ve_id) {
		this.ve_id = ve_id;
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

	public String getVe_add1() {
		return ve_add1;
	}

	public void setVe_add1(String ve_add1) {
		this.ve_add1 = ve_add1;
	}

	public String getVe_contact() {
		return ve_contact;
	}

	public void setVe_contact(String ve_contact) {
		this.ve_contact = ve_contact;
	}

	public String getVe_tel() {
		return ve_tel;
	}

	public void setVe_tel(String ve_tel) {
		this.ve_tel = ve_tel;
	}

	public String getVe_shortname() {
		return ve_shortname;
	}

	public void setVe_shortname(String ve_shortname) {
		this.ve_shortname = ve_shortname;
	}

	public Short getVe_b2benable() {
		return ve_b2benable;
	}

	public void setVe_b2benable(Short ve_b2benable) {
		this.ve_b2benable = ve_b2benable;
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

	public int getB2b() {
		return b2b;
	}

	public void setB2b(int b2b) {
		this.b2b = b2b;
	}

	public Long getB2b_vendor_id() {
		return b2b_vendor_id;
	}

	public void setB2b_vendor_id(Long b2b_vendor_id) {
		this.b2b_vendor_id = b2b_vendor_id;
	}

	public String getVe_legalman() {
		return ve_legalman;
	}

	public void setVe_legalman(String ve_legalman) {
		this.ve_legalman = ve_legalman;
	}

	public String getVe_email() {
		return ve_email;
	}

	public void setVe_email(String ve_email) {
		this.ve_email = ve_email;
	}

	public String getVe_mobile() {
		return ve_mobile;
	}

	public void setVe_mobile(String ve_mobile) {
		this.ve_mobile = ve_mobile;
	}

	public String getVe_businessrange() {
		return ve_businessrange;
	}

	public void setVe_businessrange(String ve_businessrange) {
		this.ve_businessrange = ve_businessrange;
	}

	public String getVe_industry() {
		return ve_industry;
	}

	public void setVe_industry(String ve_industry) {
		this.ve_industry = ve_industry;
	}

}
