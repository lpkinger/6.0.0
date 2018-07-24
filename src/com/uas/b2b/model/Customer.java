package com.uas.b2b.model;

/**
 * erp的customer信息
 * 
 * @author hejq
 * @time 创建时间：2017年6月7日
 */
public class Customer {

	/**
	 * id
	 */
	private Integer cu_id;

	/**
	 * 客户编号
	 */
	private String cu_code;

	/**
	 * 客户名称
	 */
	private String cu_name;

	/**
	 * 客户地址
	 */
	private String cu_add1;

	/**
	 * 客户联系人
	 */
	private String cu_contact;

	/**
	 * 客户电话
	 */
	private String cu_tel;

	/**
	 * 客户简称
	 */
	private String cu_shortname;
	/**
	 * 营业执照
	 */
	private String cu_businesscode;
	/**
	 * 法定代表人
	 */
	private String cu_lawman;
	/**
	 * 邮箱
	 */
	private String cu_email;
	/**
	 *开关 
	 */
	private Short customerSwitch;
	/**
	 * 行业
	 * @return
	 */
	private String cu_industry;
	/**
	 * 经营范围
	 * @return
	 */
	private String cu_mainbusiness;

	public String getCu_industry() {
		return cu_industry;
	}

	public void setCu_industry(String cu_industry) {
		this.cu_industry = cu_industry;
	}

	public String getCu_mainbusiness() {
		return cu_mainbusiness;
	}

	public void setCu_mainbusiness(String cu_mainbusiness) {
		this.cu_mainbusiness = cu_mainbusiness;
	}

	public Short getCustomerSwitch() {
		return customerSwitch;
	}

	public void setCustomerSwitch(Short customerSwitch) {
		this.customerSwitch = customerSwitch;
	}

	/**
	 * is b2b
	 * @return
	 */
	private int b2b;
	private String cu_uu;
	private Long b2b_vendor_id;
	public Integer getCu_id() {
		return cu_id;
	}

	public void setCu_id(Integer cu_id) {
		this.cu_id = cu_id;
	}

	public String getCu_code() {
		return cu_code;
	}

	public void setCu_code(String cu_code) {
		this.cu_code = cu_code;
	}

	public String getCu_name() {
		return cu_name;
	}

	public void setCu_name(String cu_name) {
		this.cu_name = cu_name;
	}

	public String getCu_add1() {
		return cu_add1;
	}

	public void setCu_add1(String cu_add1) {
		this.cu_add1 = cu_add1;
	}

	public String getCu_contact() {
		return cu_contact;
	}

	public void setCu_contact(String cu_contact) {
		this.cu_contact = cu_contact;
	}

	public String getCu_tel() {
		return cu_tel;
	}

	public void setCu_tel(String cu_tel) {
		this.cu_tel = cu_tel;
	}

	public String getCu_shortname() {
		return cu_shortname;
	}

	public void setCu_shortname(String cu_shortname) {
		this.cu_shortname = cu_shortname;
	}

	public int getB2b() {
		return b2b;
	}

	public void setB2b(int b2b) {
		this.b2b = b2b;
	}

	public String getCu_uu() {
		return cu_uu;
	}

	public void setCu_uu(String cu_uu) {
		this.cu_uu = cu_uu;
	}

	public String getCu_businesscode() {
		return cu_businesscode;
	}

	public void setCu_businesscode(String cu_businesscode) {
		this.cu_businesscode = cu_businesscode;
	}

	public Long getB2b_vendor_id() {
		return b2b_vendor_id;
	}

	public void setB2b_vendor_id(Long b2b_vendor_id) {
		this.b2b_vendor_id = b2b_vendor_id;
	}

	public String getCu_lawman() {
		return cu_lawman;
	}

	public void setCu_lawman(String cu_lawman) {
		this.cu_lawman = cu_lawman;
	}

	public String getCu_email() {
		return cu_email;
	}

	public void setCu_email(String cu_email) {
		this.cu_email = cu_email;
	}

}
