package com.uas.b2b.model;

import com.alibaba.fastjson.annotation.JSONField;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 招标方答疑单 （投标单关联）
 * Created by dongbw
 * 17/09/07 18:02.
 */
public class SaleTenderAnswer implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 答疑编号
     */
    private String code;

    /**
     * 招标企业UU
     */
    private Long enUU;

    /**
     * 招标企业
     */
    private Enterprise enterprise;
    
    /**
     * 招标编号
     */
    private Long tenderId;

    /**
     * 招标编号
     */
    private String tenderCode;

    /**
     * 招标标题
     */
    private String tenderTitle;

    /**
     * 答疑截止时间
     */
    private Date questionEndDate;

    /**
     * 录入时间
     */
    private Date inDate;

    /**
     * 录入人
     */
    private String recorder;
    
    /**
     * UAS审核人
     */
    private String auditMan;

    /**
     * UAS审核时间
     */
    private Date auditDate;

    /**
     * 回复时间
     */
    private Date replyDate;


    /**
     * 答疑备注
     */
    private String remark;

    /**
     * 处理状态
     */
    private Short status;
    
    private String auditstatus;
    
    private String auditstatuscode;
    
    private String attachs;

    /**
     * 投标提问单
     */
    private Set<SaleTenderQuestion> saleTenderQuestions;
    
    

    /**
     * 答疑附件
     */
    private Set<TenderAttach> answerAttaches;

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

    public Long getEnUU() {
        return enUU;
    }

    public void setEnUU(Long enUU) {
        this.enUU = enUU;
    }

	public Long getTenderId() {
		return tenderId;
	}

	public void setTenderId(Long tenderId) {
		this.tenderId = tenderId;
	}

	public String getTenderCode() {
        return tenderCode;
    }

    public void setTenderCode(String tenderCode) {
        this.tenderCode = tenderCode;
    }

    public String getTenderTitle() {
        return tenderTitle;
    }

    public void setTenderTitle(String tenderTitle) {
        this.tenderTitle = tenderTitle;
    }

    public Date getQuestionEndDate() {
        return questionEndDate;
    }

    public void setQuestionEndDate(Date questionEndDate) {
        this.questionEndDate = questionEndDate;
    }

    public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }


    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public String getRecorder() {
        return recorder;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    public String getAuditMan() {
		return auditMan;
	}

	public void setAuditMan(String auditMan) {
		this.auditMan = auditMan;
	}

	public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public Set<SaleTenderQuestion> getSaleTenderQuestions() {
        return saleTenderQuestions;
    }

    public void setSaleTenderQuestions(Set<SaleTenderQuestion> saleTenderQuestions) {
        this.saleTenderQuestions = saleTenderQuestions;
    }

    public Set<TenderAttach> getAnswerAttaches() {
        return answerAttaches;
    }

    public void setAnswerAttaches(Set<TenderAttach> answerAttaches) {
        this.answerAttaches = answerAttaches;
    }

	public String getAuditstatus() {
		return auditstatus;
	}

	public void setAuditstatus(String auditstatus) {
		this.auditstatus = auditstatus;
	}

	public String getAuditstatuscode() {
		return auditstatuscode;
	}

	public void setAuditstatuscode(String auditstatuscode) {
		this.auditstatuscode = auditstatuscode;
	}

	public String getAttachs() {
		return attachs;
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}
}
