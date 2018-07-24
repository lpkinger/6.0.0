package com.uas.b2b.model;


import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

/**
 * 投标单主表
 * <p>
 * Created by dongbw on 17/03/07 11:16.
 */

public class SaleTenderErp implements Serializable {

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
	 * 招标企业名称
	 */
	private String enname;

    /**
     * 单据最后修改时间
     */
    private Date modified;

    /**
     * 招标单录入日期
     */
    private Date date;

    /**
     * 招标方联系人
     */
    private String user;

    /**
     * 招标方联系人电话
     */
    private String usertel;

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
    private Set<TenderAttach> tenderAttachs;

    /**
     * 投标附件
     */
    private Set<TenderAttach> bidAttaches;

    /**
     * 投标明细
     */
    private Set<SaleTenderItemErp> saleTenderItems;

    /**
     * 投标企业uu
     */
    private Long vendUU;

    /**
     * 投标企业基本信息
     */
    private EnterpriseBaseInfo enterpriseBaseInfo;

    /**
     * 审核状态(0代表已提交，1代表已审核，uas审批流用，平台发布单据为空)
     */
    private Short auditStatus;
    
    private String attachs;
    
    /**
     * 是否截止(0未截止,1已截止)
     */
    private Short overdue;

    /**
     * 是否公布结果（0未公布，1已公布）
     */
    private Short result;
    
	private Object st_status;
	private Object st_auditman;
	private Object st_auditdate;
	private Object st_statuscode;

    public SaleTenderErp() {
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
    

    public String getEnname() {
    	enname = enterprise.getEnName();
		return enname;
	}


	public void setEnName(String enname) {
		this.enname = enname;
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

    public String getUsertel() {
        return usertel;
    }

    public void setUserTel(String userTel) {
        this.usertel = userTel;
    }

    @JsonIgnore
    @JSONField(serialize = false)
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

	public String getEndDate() {
        return DateUtil.format(endDate, Constant.YMD);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPublishDate() {
        return DateUtil.format(publishDate, Constant.YMD);
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
        return (short) (ifOpen==1?-1:0);
    }

    public void setIfOpen(Short ifOpen) {
        this.ifOpen = ifOpen;
    }

    public Short getIfTax() {
        return (short) (ifTax==1?-1:0);
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

	@JsonIgnore
    @JSONField(serialize = false)
    public Set<TenderAttach> getBidAttaches() {
        return bidAttaches;
    }

    public void setBidAttaches(Set<TenderAttach> bidAttaches) {
        this.bidAttaches = bidAttaches;
    }
    
    @JsonIgnore
    @JSONField(serialize = false)
    public Set<TenderAttach> getTenderAttachs() {
		return tenderAttachs;
	}

	public void setTenderAttachs(Set<TenderAttach> tenderAttachs) {
		this.tenderAttachs = tenderAttachs;
	}


	public Set<SaleTenderItemErp> getSaleTenderItems() {
		if (saleTenderItems!=null) {
			for (SaleTenderItemErp sErp : saleTenderItems) {
				sErp.setIndex(sErp.getTenderProd().getIndex());
				sErp.setProdTitle(sErp.getTenderProd().getProdTitle());
				sErp.setProdSpec(sErp.getTenderProd().getProdSpec());
				sErp.setProdCode(sErp.getTenderProd().getProdCode());
				sErp.setBrand(sErp.getTenderProd().getBrand());
				sErp.setUnit(sErp.getTenderProd().getUnit());
				sErp.setQty(sErp.getTenderProd().getQty());
			}
		}
        return saleTenderItems;
    }

    public void setSaleTenderItems(Set<SaleTenderItemErp> saleTenderItems) {
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


	public String getAttachs() {
		return attachs;
	}


	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}
	
	public Short getOverdue() {
		if (DateUtil.overDate(endDate, 1).before(new Date())) {
			overdue = 1;
		}else {
			overdue = 0;
		}
		return overdue;
	}


	public void setOverdue(Short overdue) {
		this.overdue = overdue;
	}

	public Short getResult() {
		if (DateUtil.overDate(publishDate, 1).before(new Date())) {
			result = 1;
		}else {
			result = 0;
		}
		return result;
	}

	public void setResult(Short result) {
		this.result = result;
	}


	public Object getSt_status() {
		return st_status;
	}


	public void setSt_status(Object st_status) {
		this.st_status = st_status;
	}

	public Object getSt_auditman() {
		return st_auditman;
	}


	public void setSt_auditman(Object st_auditman) {
		this.st_auditman = st_auditman;
	}


	public Object getSt_auditdate() {
		return st_auditdate;
	}


	public void setSt_auditdate(Object st_auditdate) {
		this.st_auditdate = st_auditdate;
	}


	public Object getSt_statuscode() {
		return st_statuscode;
	}


	public void setSt_statuscode(Object st_statuscode) {
		this.st_statuscode = st_statuscode;
	}
    
}
