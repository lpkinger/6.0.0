package com.uas.b2b.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

/**
 *  招标单信息(因为存在根据日期判断状态，需要使用定时任务，采用视图)
 * Created by dongbw on 17/03/07 11:16.
 */

public class PurchaseTender implements Serializable {
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
   	 * 招标企业名称
   	 */
   	private String enName;

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
     * 发布状态（保存为0，发布为1）
     */
    private Short isPublish;
    
    private String attachs;

    /**
     * 招标产品明细
     */
    private List<PurchaseTenderProd> purchaseTenderProds;

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
    
	private Short turned;
	
	 /**
     * 审核状态(0代表已提交，1代表已审核，uas审批流用，平台发布单据为空)
     */
    private Short auditStatus;
    
    /**
     * 已投标的企业数
     */
    private Integer bidEnNum;
    
    /**
     * UAS评标单基础字段
     */
	private Object pt_status;
	private Object pt_recordman;
	private Object pt_indate;
	private Object pt_auditman;
	private Object pt_auditdate;
	private Object pt_statuscode;
	private Object pt_attachs;
	private Object pt_turnPurchase;
	
	/**
     * UAS招标单基础字段
     */
	private Object tt_status;
	private Object tt_auditman;
	private Object tt_auditdate;
	private Object tt_statuscode;
    
	private Long saleId;
	
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
    
	public String getEnName() {
		enName = enterprise.getEnName();
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
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

    public void setUserTel(String userTel){
    	this.userTel = userTel;
    }

    public String getUserTel() {
		return userTel;
	}

	public Long getEnUU() {
        return enUU;
    }

    public void setEnUU(Long enUU) {
        this.enUU = enUU;
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

    public String getStatus() {
    	if ("已结标".equals(status)&&auditStatus!=null&&auditStatus==0) {
    		status = "待评标";
		}
    	if("待评标".equals(status)&&overdue==0){
    		status = "待投标";
    	}
    	if (("待投标".equals(status)&&overdue==1)||(result==1&&!"已结标".equals(status))||
    			("已结标".equals(status)&&auditStatus!=null&&auditStatus==0)) {
			status = "流标";
		}
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

    public Short getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Short isPublish) {
        this.isPublish = isPublish;
    }

    public List<PurchaseTenderProd> getPurchaseTenderProds() {
        return purchaseTenderProds;
    }

    public void setPurchaseTenderProds(List<PurchaseTenderProd> purchaseTenderProds) {
        this.purchaseTenderProds = purchaseTenderProds;
    }

    public Set<TenderAttach> getTenderAttaches() {
        return tenderAttaches;
    }
    

    public Short getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Short auditStatus) {
		this.auditStatus = auditStatus;
	}

	@JsonIgnore
    @JSONField(serialize = false)
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

	public Short getTurned() {
		return turned;
	}

	public void setTurned(Short turned) {
		this.turned = turned;
	}

	public String getAttachs() {
		return attachs;
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}
	
	public Integer getBidEnNum() {
		return bidEnNum;
	}

	public void setBidEnNum(Integer bidEnNum) {
		this.bidEnNum = bidEnNum;
	}

	public Object getPt_status() {
		return pt_status;
	}

	public void setPt_status(Object pt_status) {
		this.pt_status = pt_status;
	}

	public Object getPt_recordman() {
		return pt_recordman;
	}

	public void setPt_recordman(Object pt_recordman) {
		this.pt_recordman = pt_recordman;
	}

	public Object getPt_indate() {
		return pt_indate;
	}

	public void setPt_indate(Object pt_indate) {
		this.pt_indate = pt_indate;
	}

	public Object getPt_auditman() {
		return pt_auditman;
	}

	public void setPt_auditman(Object pt_auditman) {
		this.pt_auditman = pt_auditman;
	}

	public Object getPt_auditdate() {
		return pt_auditdate;
	}

	public void setPt_auditdate(Object pt_auditdate) {
		this.pt_auditdate = pt_auditdate;
	}

	public Object getPt_statuscode() {
		return pt_statuscode;
	}

	public void setPt_statuscode(Object pt_statuscode) {
		this.pt_statuscode = pt_statuscode;
	}
	
	public Object getPt_attachs() {
		return pt_attachs;
	}

	public void setPt_attachs(Object pt_attachs) {
		this.pt_attachs = pt_attachs;
	}

	public Object getPt_turnPurchase() {
		return pt_turnPurchase;
	}

	public void setPt_turnPurchase(Object pt_turnPurchase) {
		this.pt_turnPurchase = pt_turnPurchase;
	}

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	public Object getTt_status() {
		return tt_status;
	}

	public void setTt_status(Object tt_status) {
		this.tt_status = tt_status;
	}

	public Object getTt_auditman() {
		return tt_auditman;
	}

	public void setTt_auditman(Object tt_auditman) {
		this.tt_auditman = tt_auditman;
	}

	public Object getTt_auditdate() {
		return tt_auditdate;
	}

	public void setTt_auditdate(Object tt_auditdate) {
		this.tt_auditdate = tt_auditdate;
	}

	public Object getTt_statuscode() {
		return tt_statuscode;
	}

	public void setTt_statuscode(Object tt_statuscode) {
		this.tt_statuscode = tt_statuscode;
	}

}


