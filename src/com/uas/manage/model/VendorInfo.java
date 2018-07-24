package com.uas.manage.model;

import com.uas.erp.core.support.KeyEntity;

/**
 * 客户维护的供应商资料
 */
public class VendorInfo extends KeyEntity{
	
	private Long id;//id
	private Long otherUU;//供应商UU号
	private String otherName;//供应商名称
	private String contact;//供应商联系人
	private String contactMobile;//联系人电话
	private String contactEmail;//联系人邮箱
	private Long enUU;//客户UU号
	private Short sendStatus;//供应商上传状态
	private String businessCode;//供应商营业执照号
	private String otherType;//类型
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getOtherUU() {
		return otherUU;
	}


	public void setOtherUU(Long otherUU) {
		this.otherUU = otherUU;
	}


	public String getOtherName() {
		return otherName;
	}


	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}


	public String getContact() {
		return contact;
	}


	public void setContact(String contact) {
		this.contact = contact;
	}


	public String getContactMobile() {
		return contactMobile;
	}


	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}


	public String getContactEmail() {
		return contactEmail;
	}


	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}


	public Long getEnUU() {
		return enUU;
	}


	public void setEnUU(Long enUU) {
		this.enUU = enUU;
	}


	public Short getSendStatus() {
		return sendStatus;
	}


	public void setSendStatus(Short sendStatus) {
		this.sendStatus = sendStatus;
	}




	public String getBusinessCode() {
		return businessCode;
	}


	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}


	public String getOtherType() {
		return otherType;
	}


	public void setOtherType(String otherType) {
		this.otherType = otherType;
	}


	@Override
	public Object getKey() {
		return this.id;
	}


}
