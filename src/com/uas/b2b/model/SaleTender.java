package com.uas.b2b.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 投标单主表
 * <p>
 * Created by dongbw on 17/03/07 11:16.
 */

public class SaleTender implements Serializable, Comparable<SaleTender> {

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
     * 招标项目
     */
    private String title;

    /**
     * 单据最后修改时间
     */
    private Date modified;

    /**
     * 招标单录入日期
     */
    private Date date;

    /**
     * 投标方联系人
     */
    private String user;

    /**
     * 投标方联系人电话
     */
    private String userTel;
    
    /**
     * 投标方联系人邮箱
     */
    private String userEmail;

    /**
     * 招标企业uu
     */
    private Long enUU;

    /**
     * 招标企业基本信息
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
     * 结果公布日期
     */
    private Date publishDate;

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
     * 招标类型（1全包，0甲供料）
     */
    private Short ifAll;

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
     * 投标单状态
     */
    private String status;
    
    /**
     *项目周期
     */
    private Long cycle;
    
    /**
     * 税率
     */
    private Long taxrate;
    
    /**
     * 总报价
     */
    private Double totalMoney;
    
    /**
     * 投标附件
     */
    private Set<Attach> bidAttaches;

    /**
     * 投标明细
     */
    private Set<SaleTenderItem> saleTenderItems;

    /**
     * 投标企业uu
     */
    private Long vendUU;
    
    /**
     * 审核状态(0代表已提交，1代表已审核，uas审批流用，平台发布单据为空)
     */
    private Short auditStatus;

    /**
     * 投标企业基本信息
     */
    private EnterpriseBaseInfo enterpriseBaseInfo;


    public SaleTender() {
    }


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
    
    public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public String getQuestionEndDate() {
		return DateUtil.format(questionEndDate, Constant.YMD);
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

    public Short getIfAll() {
		return ifAll;
	}

	public void setIfAll(Short ifAll) {
		this.ifAll = ifAll;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCycle() {
		return cycle;
	}

	public void setCycle(Long cycle) {
		this.cycle = cycle;
	}

	public Long getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(Long taxrate) {
		this.taxrate = taxrate;
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Set<Attach> getBidAttaches() {
        return bidAttaches;
    }

    public void setBidAttaches(Set<Attach> bidAttaches) {
        this.bidAttaches = bidAttaches;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public Set<SaleTenderItem> getSaleTenderItems() {
        return saleTenderItems;
    }

    public void setSaleTenderItems(Set<SaleTenderItem> saleTenderItems) {
        this.saleTenderItems = saleTenderItems;
    }

    public EnterpriseBaseInfo getEnterpriseBaseInfo() {
        return enterpriseBaseInfo;
    }

    public void setEnterpriseBaseInfo(EnterpriseBaseInfo enterpriseBaseInfo) {
        this.enterpriseBaseInfo = enterpriseBaseInfo;
    }

    public Long getEnUU() {
        return enUU;
    }

    public void setEnUU(Long enUU) {
        this.enUU = enUU;
    }

    public Long getVendUU() {
        return vendUU;
    }

    public void setVendUU(Long vendUU) {
        this.vendUU = vendUU;
    }

    public Short getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Short auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Override
    public int compareTo(SaleTender o) {
        return this.getId().compareTo(o.getId());
    }

}
