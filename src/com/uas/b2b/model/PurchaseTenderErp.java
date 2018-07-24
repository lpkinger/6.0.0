package com.uas.b2b.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

/**
 *  招标单信息(因为存在根据日期判断状态，需要使用定时任务，采用视图)
 * Created by dongbw on 17/03/07 11:16.
 */

public class PurchaseTenderErp implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 招标编号
     */
    private String code;

    /**
     * 招标标题
     */
    private String title;

    /**
     * 单据最后时间
     */
    private Date modified;

    /**
     * 录入日期
     */
    private Date date;

    /**
     * 联系人
     */
    private String user;

    /**
     * 联系人uu
     */
    private String userTel;

    /**
     * 招标企业uu
     */
    private Long enUU;
    /**
     * 企业基本信息
     */
    private Enterprise enterprise;

    /**
     * 提问截止时间
     */
    private Date questionEndDate;
    
    /**
     * 投标截止日期
     */
    private Date endDate;

    /**
     *  结果公布日期
     */
    private Date publishDate;

    /**
     * 状态（待发布，待投标，待评标，已结标）
     */
    private String status;


    /**
     * 币别
     */
    private String currency;

    /**
     * 是否开放报名（1为开放，0为指定供应商）
     */
    private Short ifOpen;

    /**
     * 是否含税（1含税，0不含税）
     */
    private Short ifTax;

    /**
     * 收货地址
     */
    private String shipAddress;

    /**
     * 付款方式
     */
    private String payment;

    /**
     * 发票类型（0表示不要发票， 1表示增值税普通发票， 2表示增值税专用发票）
     */
    private Short invoiceType;

    /**
     * 证照要求
     */
    private String certificate;

    /**
     * 发布状态（保存为0，发布为1）
     */
    private Short isPublish;

    /**
     * 招标附件
     */
    private Set<TenderAttach> tenderAttaches;

    /**
     * 是否截止(0未截止,1已截止)
     */
    private Short overdue;

    /**
     * 是否公布结果（0未公布，1已公布）
     */
    private Short result;


    /**
     * 审核状态(0代表已提交，1代表已审核，uas审批流用，平台发布单据为空)
     */
    private Short auditStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public Long getEnUU() {
        return enUU;
    }

    public void setEnUU(Long enUU) {
        this.enUU = enUU;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public String getQuestionEndDate() {
		return questionEndDate==null?null:DateUtil.format(questionEndDate, Constant.YMD_HMS);
	}

	public void setQuestionEndDate(Date questionEndDate) {
		this.questionEndDate = questionEndDate;
	}

	public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Short getIfOpen() {
        return ifOpen;
    }

    public void setIfOpen(Short ifOpen) {
        this.ifOpen = ifOpen;
    }

    public Short getIfTax() {
        return ifTax;
    }

    public void setIfTax(Short ifTax) {
        this.ifTax = ifTax;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Short getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Short invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public Short getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Short isPublish) {
        this.isPublish = isPublish;
    }

    public Set<TenderAttach> getTenderAttaches() {
        return tenderAttaches;
    }

    public void setTenderAttaches(Set<TenderAttach> tenderAttaches) {
        this.tenderAttaches = tenderAttaches;
    }

    public Short getOverdue() {
        return overdue;
    }

    public void setOverdue(Short overdue) {
        this.overdue = overdue;
    }

    public Short getResult() {
        return result;
    }

    public void setResult(Short result) {
        this.result = result;
    }

    public Short getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Short auditStatus) {
        this.auditStatus = auditStatus;
    }
}


