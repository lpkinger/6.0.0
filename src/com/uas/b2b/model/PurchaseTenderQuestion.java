package com.uas.b2b.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.uas.b2b.model.Enterprise;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 投标方提问单 （招标单关联）
 * Created by dongbw
 * 17/09/07 18:17.
 */
public class PurchaseTenderQuestion implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 提问编号
     */
    private String code;

    /**
     * 投标企业UU
     */
    private Long vendUU;

    /**
     * 投标企业
     */
    private Enterprise vendor;

    /**
     * 提出时间
     */
    private Date inDate;

    /**
     * 回复时间
     */
    private Date replyDate;

    /**
     * 提问主题
     */
    private String topic;

    /**
     * 处理状态
     */
    private Short status;

    /**
     * 咨询内容
     */
    private String content;
    
    private String attachs;

    /**
     * 招标答疑单
     */
    private PurchaseTenderAnswer purchaseTenderAnswer;

    /**
     * 提问附件
     */
    private Set<TenderAttach> questionAttaches;

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


    public Long getVendUU() {
        return vendUU;
    }

    public void setVendUU(Long vendUU) {
        this.vendUU = vendUU;
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @JsonIgnore
    @JSONField(serialize = false)
    public Enterprise getVendor() {
        return vendor;
    }

    public void setVendor(Enterprise vendor) {
        this.vendor = vendor;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public PurchaseTenderAnswer getPurchaseTenderAnswer() {
        return purchaseTenderAnswer;
    }

    public void setPurchaseTenderAnswer(PurchaseTenderAnswer purchaseTenderAnswer) {
        this.purchaseTenderAnswer = purchaseTenderAnswer;
    }

    public Set<TenderAttach> getQuestionAttaches() {
        return questionAttaches;
    }

    public void setQuestionAttaches(Set<TenderAttach> questionAttaches) {
        this.questionAttaches = questionAttaches;
    }

	public String getAttachs() {
		return attachs;
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}
    
}
