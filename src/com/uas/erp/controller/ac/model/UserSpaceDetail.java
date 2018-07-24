package com.uas.erp.controller.ac.model;

import java.io.Serializable;

/**
 * 详细企业资料
 * 
 * <pre>
 * 应涵盖全部企业信息，满足所有应用需求
 * </pre>
 * 
 * @author yingp
 * 
 */
public class UserSpaceDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String shortName;
	private String businessCode;
	private String businessCodeImage;
	private String address;
	private String province;
	private String city;
	private String district;
	private String street;
	private String corporation;
	private String fax;
	private String tel;
	private Long registerDate;
	private String type;
	private String url;
	private String area;
	private String industry;
	private String domain;
	private String adminName;
	private String adminTel;
	private String adminEmail;
	private Integer status;
	private Integer approveStatus;
	private String applyApps;
	private String errMsg;
	private String logoImage;
	private Integer requestStatus;
	private Integer method;
	private String profession;
	private String tags;
	private Long uu;
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

	/**
	 * 注册全称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 简称
	 * 
	 * @return
	 */
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * 注册号
	 * 
	 * @return
	 */
	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	/**
	 * 注册地址
	 * 
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 法人名
	 * 
	 * @return
	 */
	public String getCorporation() {
		return corporation;
	}

	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}

	/**
	 * 传真
	 * 
	 * @return
	 */
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * 注册（优软平台）的日期
	 * 
	 * @return
	 */
	public Long getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Long registerDate) {
		this.registerDate = registerDate;
	}

	/**
	 * 企业类型
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 官网
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 区域
	 * 
	 * @return
	 */
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * 主营类型
	 * 
	 * @return
	 */
	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/**
	 * （优软平台分配的）二级域名
	 * 
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * 审核状态
	 * 
	 * @return
	 */
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 企业联系电话
	 * 
	 * @return
	 */
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * 营业执照附件
	 * 
	 * @return
	 */
	public String getBusinessCodeImage() {
		return businessCodeImage;
	}

	public void setBusinessCodeImage(String businessCodeImage) {
		this.businessCodeImage = businessCodeImage;
	}

	/**
	 * 管理员名
	 * 
	 * @return
	 */
	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	/**
	 * 管理员电话
	 * 
	 * @return
	 */
	public String getAdminTel() {
		return adminTel;
	}

	public void setAdminTel(String adminTel) {
		this.adminTel = adminTel;
	}

	/**
	 * 管理员邮箱
	 * 
	 * @return
	 */
	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	/**
	 * 申请的应用
	 * 
	 * @return
	 */
	public String getApplyApps() {
		return applyApps;
	}

	public void setApplyApps(String applyApps) {
		this.applyApps = applyApps;
	}

	/**
	 * 错误信息
	 * 
	 * @return
	 */
	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * 省
	 * 
	 * @return
	 */
	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * 市
	 * 
	 * @return
	 */
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 区
	 * 
	 * @return
	 */
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	/**
	 * 街道
	 * 
	 * @return
	 */
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * logo图标
	 * 
	 * @return
	 */
	public String getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(String logoImage) {
		this.logoImage = logoImage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 验证状态
	 * @return
	 */
	public Integer getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(Integer approveStatus) {
		this.approveStatus = approveStatus;
	}

	/**
	 * 申请状态
	 * 
	 * @return
	 */
	public Integer getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(Integer requestStatus) {
		this.requestStatus = requestStatus;
	}

	/**
	 * 方式（主动还是被动）<br>
	 * 1: 主动发出 <br>
	 * 0： 对方发出
	 * 
	 * @param method
	 */
	public Integer getMethod() {
		return method;
	}

	public void setMethod(Integer method) {
		this.method = method;
	}

	public Long getUu() {
		return uu;
	}

	public void setUu(Long uu) {
		this.uu = uu;
	}

}
