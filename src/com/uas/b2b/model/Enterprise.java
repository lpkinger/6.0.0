package com.uas.b2b.model;

import java.util.Date;

public class Enterprise {

	/**
	 * 公司名称
	 */
	private String enName;
	/**
	 * 简称
	 */
	
	private String enShortname;
	/**
	 * 公司类型
	 */
	
	private String enType;
	/**
	 * 企业注册地区
	 */
	
	private String enArea;
	/**
	 * 状态
	 */
	
	private Short enStatus;
	/**
	 * 传输到管理平台的状态
	 */
	
	private Short enSendStatus;
	/**
	 * 注册地址
	 */
	
	private String enAddress;
	/**
	 * 默认送货地址
	 */
	
	private String enDeliverAddr;
	/**
	 * 公司电话
	 */
	
	private String enTel;
	/**
	 * 传真
	 */
	
	private String enFax;
	/**
	 * 公司邮箱
	 */
	
	private String enEmail;
	/**
	 * 企业UU
	 */
	private Long uu;
	/**
	 * 公司法人
	 */
	private String enCorporation;
	/**
	 * 商业登记证号
	 */
	private String enBussinessCode;
	
	/**
	 * 纳税人识别号
	 */
	private String enTaxcode;
	/**
	 * 注册资本
	 */
	private String enRegistercapital;
	/**
	 * 公司主页地址
	 */
	private String enUrl;
	/**
	 * 注册时间
	 */
	private Date enDate;

	/**
	 * 管理员UU号
	 */
	private Long enAdminuu;
	/**
	 * 管理员密码
	 */
	private String enAdminPassword;
	/**
	 * 管理员名称
	 */
	private String enAdminName;
	/**
	 * 管理员手机号
	 */
	private String enAdminTel;
	/**
	 * 管理员邮箱
	 */
	private String enAdminEmail;
	/**
	 * 所属行业
	 */
	private String enIndustry;
	
	/**
	 * 后台管理Id
	 */
	private Long enMasterId;
	/**
	 * 行业
	 * @return
	 */
	private String profession;
	/**
	 * 经营范围
	 * @return
	 */
	private String tags;
	/**
	 * 联系人
	 */
	private String contactMan;

	/**
	 * 联系人电话
	 */
	private String contactTel;

	/**
	 * 联系人邮箱
	 */
	private String contactEmail;
	/**
	 * 币别
	 */
	
	private String enCurrency;
	
	public String getEnCurrency() {
		return enCurrency;
	}
	public void setEnCurrency(String enCurrency) {
		this.enCurrency = enCurrency;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Long getEnMasterId() {
		return enMasterId;
	}
	public void setEnMasterId(Long enMasterId) {
		this.enMasterId = enMasterId;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getEnShortname() {
		return enShortname;
	}
	public void setEnShortname(String enShortname) {
		this.enShortname = enShortname;
	}
	public String getEnType() {
		return enType;
	}
	public void setEnType(String enType) {
		this.enType = enType;
	}
	public String getEnArea() {
		return enArea;
	}
	public void setEnArea(String enArea) {
		this.enArea = enArea;
	}
	public Short getEnStatus() {
		return enStatus;
	}
	public void setEnStatus(Short enStatus) {
		this.enStatus = enStatus;
	}
	public Short getEnSendStatus() {
		return enSendStatus;
	}
	public void setEnSendStatus(Short enSendStatus) {
		this.enSendStatus = enSendStatus;
	}
	public String getEnAddress() {
		return enAddress;
	}
	public void setEnAddress(String enAddress) {
		this.enAddress = enAddress;
	}
	public String getEnDeliverAddr() {
		return enDeliverAddr;
	}
	public void setEnDeliverAddr(String enDeliverAddr) {
		this.enDeliverAddr = enDeliverAddr;
	}
	public String getEnTel() {
		return enTel;
	}
	public void setEnTel(String enTel) {
		this.enTel = enTel;
	}
	public String getEnFax() {
		return enFax;
	}
	public void setEnFax(String enFax) {
		this.enFax = enFax;
	}
	public String getEnEmail() {
		return enEmail;
	}
	public void setEnEmail(String enEmail) {
		this.enEmail = enEmail;
	}
	public String getEnCorporation() {
		return enCorporation;
	}
	public void setEnCorporation(String enCorporation) {
		this.enCorporation = enCorporation;
	}
	public String getEnBussinessCode() {
		return enBussinessCode;
	}
	public void setEnBussinessCode(String enBussinessCode) {
		this.enBussinessCode = enBussinessCode;
	}
	public String getEnTaxcode() {
		return enTaxcode;
	}
	public void setEnTaxcode(String enTaxcode) {
		this.enTaxcode = enTaxcode;
	}
	public String getEnRegistercapital() {
		return enRegistercapital;
	}
	public void setEnRegistercapital(String enRegistercapital) {
		this.enRegistercapital = enRegistercapital;
	}
	public String getEnUrl() {
		return enUrl;
	}
	public void setEnUrl(String enUrl) {
		this.enUrl = enUrl;
	}
	public Date getEnDate() {
		return enDate;
	}
	public void setEnDate(Date enDate) {
		this.enDate = enDate;
	}
	public Long getEnAdminuu() {
		return enAdminuu;
	}
	public void setEnAdminuu(Long enAdminuu) {
		this.enAdminuu = enAdminuu;
	}
	public String getEnIndustry() {
		return enIndustry;
	}
	public void setEnIndustry(String enIndustry) {
		this.enIndustry = enIndustry;
	}
	public String getEnAdminPassword() {
		return enAdminPassword;
	}
	public void setEnAdminPassword(String enAdminPassword) {
		this.enAdminPassword = enAdminPassword;
	}
	public String getEnAdminName() {
		return enAdminName;
	}
	public void setEnAdminName(String enAdminName) {
		this.enAdminName = enAdminName;
	}
	public String getEnAdminTel() {
		return enAdminTel;
	}
	public void setEnAdminTel(String enAdminTel) {
		this.enAdminTel = enAdminTel;
	}
	public String getEnAdminEmail() {
		return enAdminEmail;
	}
	public void setEnAdminEmail(String enAdminEmail) {
		this.enAdminEmail = enAdminEmail;
	}
	public Long getUu() {
		return uu;
	}
	public void setUu(Long uu) {
		this.uu = uu;
	}
	public String getContactMan() {
		return contactMan;
	}
	public void setContactMan(String contactMan) {
		this.contactMan = contactMan;
	}
	public String getContactTel() {
		return contactTel;
	}
	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	
}
