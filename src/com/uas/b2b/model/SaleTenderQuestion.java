package com.uas.b2b.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 投标方提问单 （投标单关联）
 * Created by dongbw
 * 17/09/07 18:10.
 */
public class SaleTenderQuestion implements Serializable {

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
     * 提问内容
     */
    private String content;
    
    private String attachs;


    /**
     * 招标答疑单
     */
    private SaleTenderAnswer saleTenderAnswer;

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
    
    @JsonIgnore
	@JSONField(serialize = false)
    public Enterprise getVendor() {
        return vendor;
    }

    public void setVendor(Enterprise vendor) {
        this.vendor = vendor;
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

    public String getAttachs() {
		return attachs;
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}

	@JsonIgnore
	@JSONField(serialize = false)
	public Set<TenderAttach> getQuestionAttaches() {
        return questionAttaches;
    }

    public void setQuestionAttaches(Set<TenderAttach> questionAttaches) {
        this.questionAttaches = questionAttaches;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public SaleTenderAnswer getSaleTenderAnswer() {
        return saleTenderAnswer;
    }

    public void setSaleTenderAnswer(SaleTenderAnswer saleTenderAnswer) {
        this.saleTenderAnswer = saleTenderAnswer;
    }
    
    public Long getTenderId() {
  		return saleTenderAnswer.getTenderId();
  	}
    
    public String getTenderCode() {
        return saleTenderAnswer.getTenderCode();
    }

    public String getTenderTitle() {
        return saleTenderAnswer.getTenderTitle();
    }
    
    public Date getQuestionEndDate() {
        return saleTenderAnswer.getQuestionEndDate();
    }

    public String getVendName() {
        return vendor.getEnName();
    }
}
