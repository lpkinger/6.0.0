package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.bind.Constant;

public class MakeChangeReply {

	private long b2b_md_id;
	/**
	 * 供应商是否同意了我的变更请求(1,0)
	 */
	private Short md_agreed;
	/**
	 * 供应商的回复备注
	 */
	private String md_replyremark;
	/**
	 * 回复时间
	 */
	private Date md_replydate;
	/**
	 * 回复人
	 */
	private String md_replyman;
	private String mc_code;
	private String ma_code;
	private Short md_detno;
	
	public long getB2b_md_id() {
		return b2b_md_id;
	}

	public void setB2b_md_id(long b2b_md_id) {
		this.b2b_md_id = b2b_md_id;
	}

	public Short getMd_agreed() {
		return md_agreed;
	}

	public void setMd_agreed(Short md_agreed) {
		this.md_agreed = md_agreed;
	}

	public String getMd_replyremark() {
		return md_replyremark;
	}

	public void setMd_replyremark(String md_replyremark) {
		this.md_replyremark = md_replyremark;
	}

	public Date getMd_replydate() {
		return md_replydate;
	}

	public void setMd_replydate(Date md_replydate) {
		this.md_replydate = md_replydate;
	}

	public String getMd_replyman() {
		return md_replyman;
	}

	public void setMd_replyman(String md_replyman) {
		this.md_replyman = md_replyman;
	}

	public String getMc_code() {
		return mc_code;
	}

	public void setMc_code(String mc_code) {
		this.mc_code = mc_code;
	}

	public Short getMd_detno() {
		return md_detno;
	}

	public void setMd_detno(Short md_detno) {
		this.md_detno = md_detno;
	}

	public String getMa_code() {
		return ma_code;
	}

	public void setMa_code(String ma_code) {
		this.ma_code = ma_code;
	}

	public MakeChangeReply(){
		
	}
	
	/**
	 * 供应商是否同意
	 * @return
	 */
	public boolean isAgreed() {
		return this.md_agreed != null && (Constant.YES == this.md_agreed || Constant.yes == this.md_agreed);
	}

}
