package com.uas.erp.controller.ac.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.b2b.model.Enterprise;
import com.uas.b2b.model.UserErpCustInfo;

/**
 * 将收到的数据进行处理，只显示对方企业的信息<br>
 * PartnershipRecord
 * 
 * @author hejq
 * @time 创建时间：2017年1月20日
 */
public class BasePartnersInfo implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * 应用
	 */
	private String appId;

	/**
	 * 申请时间
	 */
	private Date requestDate;

	/**
	 * 客户的企业名称
	 */
	private String vendName;

	/**
	 * 客户的标识（如UU号等）
	 */
	private String vendUID;

	/**
	 * 审核人姓名
	 */
	private String vendUserName;

	/**
	 * 审核人联系方式
	 */
	private String vendUserTel;

	/**
	 * 审核人邮箱
	 */
	private String vendUserEmail;

	/**
	 * 审核人标志（uu号等）
	 */
	private String vendUserCode;

	/**
	 * 审核时间
	 */
	private Date operateDate;

	/**
	 * 操作方式，接收or发<br>
	 * send: 1<br>
	 * get: 0
	 */
	private Short method;

	/**
	 * 状态
	 */
	private Integer statusCode;

	/**
	 * 申请人
	 */
	private String custUserName;

	/**
	 * 申请人联系方式
	 */
	private String custUserTel;

	/**
	 * 原因
	 */
	private String reason;
	
	/**
	 * UU
	 * @return
	 */
	/**
	 * 是否是供应商
	 */
	private Short vendor;

	/**
	 * 是否是客户
	 */
	private Short customer;
	/**
	 * 服务开关
	 */
	private Short servicerswitch;

	/**
	 * 合作伙伴企业信息
	 */
	private Enterprise enterprise;

	/**
	 * 合作伙伴联系人信息
	 */
	private UserErpCustInfo contact;
	public Short getServicerswitch() {
		return servicerswitch;
	}

	public void setServicerswitch(Short servicerswitch) {
		this.servicerswitch = servicerswitch;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public UserErpCustInfo getContact() {
		return contact;
	}

	public void setContact(UserErpCustInfo contact) {
		this.contact = contact;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public Long getCustId() {
		return custId;
	}

	public void setCustId(Long custId) {
		this.custId = custId;
	}

	public Short getVendswitch() {
		return vendswitch;
	}

	public void setVendswitch(Short vendswitch) {
		this.vendswitch = vendswitch;
	}

	public Short getCustswitch() {
		return custswitch;
	}

	public void setCustswitch(Short custswitch) {
		this.custswitch = custswitch;
	}

	/**
	 * 平台的供应商表的id（供应商）
	 */
	private Long vendorId;

	/**
	 * 平台供应商表的id（客户）
	 */
	private Long custId;

	/**
	 * 供应商开关
	 */
	private Short vendswitch;

	/**
	 * 客户开关
	 */
	private Short custswitch;

	/**
	 * 企业UU
	 */
	private String uu;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getVendName() {
		return vendName;
	}

	public void setVendName(String vendName) {
		this.vendName = vendName;
	}

	public String getVendUID() {
		return vendUID;
	}

	public void setVendUID(String vendUID) {
		this.vendUID = vendUID;
	}

	public String getVendUserName() {
		return vendUserName;
	}

	public void setVendUserName(String vendUserName) {
		this.vendUserName = vendUserName;
	}

	public String getVendUserTel() {
		return vendUserTel;
	}

	public void setVendUserTel(String vendUserTel) {
		this.vendUserTel = vendUserTel;
	}

	public String getVendUserEmail() {
		return vendUserEmail;
	}

	public void setVendUserEmail(String vendUserEmail) {
		this.vendUserEmail = vendUserEmail;
	}

	public String getVendUserCode() {
		return vendUserCode;
	}

	public void setVendUserCode(String vendUserCode) {
		this.vendUserCode = vendUserCode;
	}

	public Date getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(Date operateDate) {
		this.operateDate = operateDate;
	}

	public Short getMethod() {
		return method;
	}

	public void setMethod(Short method) {
		this.method = method;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getCustUserName() {
		return custUserName;
	}

	public void setCustUserName(String custUserName) {
		this.custUserName = custUserName;
	}

	public String getCustUserTel() {
		return custUserTel;
	}

	public void setCustUserTel(String custUserTel) {
		this.custUserTel = custUserTel;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "BasePartnersInfo [id=" + id + ", appId=" + appId
				+ ", requestDate=" + requestDate + ", vendName=" + vendName
				+ ", vendUID=" + vendUID + ", vendUserName=" + vendUserName
				+ ", vendUserTel=" + vendUserTel + ", vendUserEmail="
				+ vendUserEmail + ", vendUserCode=" + vendUserCode
				+ ", operateDate=" + operateDate + ", method=" + method
				+ ", statusCode=" + statusCode + ", custUserName="
				+ custUserName + ", custUserTel=" + custUserTel + ", reason="
				+ reason + ", vendor=" + vendor + ", customer=" + customer
				+ ", servicerswitch=" + servicerswitch + ", enterprise="
				+ enterprise + ", contact=" + contact + ", vendorId="
				+ vendorId + ", custId=" + custId + ", vendswitch="
				+ vendswitch + ", custswitch=" + custswitch + ", uu=" + uu
				+ "]";
	}

	public String getUu() {
		return uu;
	}

	public void setUu(String uu) {
		this.uu = uu;
	}

	public Short getVendor() {
		return vendor;
	}

	public void setVendor(Short vendor) {
		this.vendor = vendor;
	}

	public Short getCustomer() {
		return customer;
	}

	public void setCustomer(Short customer) {
		this.customer = customer;
	}

}
