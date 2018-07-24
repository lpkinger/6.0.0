package com.uas.b2b.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.b2b.model.Enterprise;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 招标方答疑单 （招标单关联）
 * Created by dongbw
 * 17/09/07 17:32.
 */
public class PurchaseTenderAnswer implements Serializable {

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
     * 招标ID
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
    private String auditman;

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
    private Set<PurchaseTenderQuestion> purchaseTenderQuestions;

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

	public String getTendercode() {
        return tenderCode;
    }

    public void setTenderCode(String tenderCode) {
        this.tenderCode = tenderCode;
    }

    public String getTendertitle() {
        return tenderTitle;
    }

    public void setTenderTitle(String tenderTitle) {
        this.tenderTitle = tenderTitle;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public Date getQuestionEndDate() {
		return questionEndDate;
	}

    
    public void setQuestionEndDate(Date questionEndDate) {
        this.questionEndDate = questionEndDate;
    }

    public String getReplydate() {
    	return replyDate==null?null:DateUtil.format(replyDate, Constant.YMD_HMS);
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }


    public Short getstatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    @JsonIgnore
    @JSONField(serialize = false)
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

    public String getAuditman() {
		return auditman;
	}

	public void setAuditman(String auditman) {
		this.auditman = auditman;
	}

	public String getAuditdate() {
		return auditDate==null?null:DateUtil.format(auditDate, Constant.YMD);
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
    public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
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

	@JsonIgnore
    @JSONField(serialize = false)
    public Set<PurchaseTenderQuestion> getPurchaseTenderQuestions() {
        return purchaseTenderQuestions;
    }

    public void setPurchaseTenderQuestions(Set<PurchaseTenderQuestion> purchaseTenderQuestions) {
        this.purchaseTenderQuestions = purchaseTenderQuestions;
    }
    
    @JsonIgnore
    @JSONField(serialize = false)
    public Set<TenderAttach> getAnswerAttaches() {
        return answerAttaches;
    }

    public void setAnswerAttaches(Set<TenderAttach> answerAttaches) {
        this.answerAttaches = answerAttaches;
    }
    
    public String getEnname(){
    	if (enterprise!=null) {
    		return enterprise.getEnName();
		}
    	return null;
    }
    
	
	public String getQuestionenddate() {
    	return questionEndDate==null?null:DateUtil.format(questionEndDate, Constant.YMD_HMS);
    }

	public String getIndate() {
		return inDate==null?null:DateUtil.format(inDate, Constant.YMD);
	}
}
