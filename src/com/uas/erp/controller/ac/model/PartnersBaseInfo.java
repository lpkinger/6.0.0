package com.uas.erp.controller.ac.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 合作关系申请的记录
 * 
 * @author hejq
 * @time 创建时间：2017年1月10日
 */
public class PartnersBaseInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 账户中心对应的id
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

	@Override
	public String toString() {
		return "PartnersBaseInfo [id=" + id + ", appId=" + appId + ", requestDate=" + requestDate + ", vendName="
				+ vendName + ", vendUID=" + vendUID + ", vendUserName=" + vendUserName + ", vendUserTel=" + vendUserTel
				+ ", vendUserEmail=" + vendUserEmail + ", vendUserCode=" + vendUserCode + ", operateDate=" + operateDate
				+ "]";
	}

}
