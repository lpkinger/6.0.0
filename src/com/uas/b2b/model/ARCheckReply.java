package com.uas.b2b.model;

import java.util.Date;

public class ARCheckReply {

	private Long id;
	private Long sourceId;
	private Double replyQty;
	private String replyRemark;
	private Date replyDate;
	
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Double getReplyQty() {
		return replyQty;
	}
	public void setReplyQty(Double replyQty) {
		this.replyQty = replyQty;
	}
	public String getReplyRemark() {
		return replyRemark;
	}
	public void setReplyRemark(String replyRemark) {
		this.replyRemark = replyRemark;
	}
	public Date getReplyDate() {
		return replyDate;
	}
	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}
