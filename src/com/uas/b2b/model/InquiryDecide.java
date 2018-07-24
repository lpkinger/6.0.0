package com.uas.b2b.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.bind.Constant;

/**
 * 买家ERP系统对报价单的采纳结果
 * 
 * @author yingp
 * 
 */
public class InquiryDecide {

	private long id_id;
	private Long b2b_qu_id;
	private String in_code;
	private Short id_detno;
	private Short id_agreed;

	@JsonIgnore
	public long getId_id() {
		return id_id;
	}

	public void setId_id(long id_id) {
		this.id_id = id_id;
	}

	public String getIn_code() {
		return in_code;
	}

	public void setIn_code(String in_code) {
		this.in_code = in_code;
	}

	public Short getId_detno() {
		return id_detno;
	}

	public void setId_detno(Short id_detno) {
		this.id_detno = id_detno;
	}

	public Short getId_agreed() {
		return id_agreed == null ? Constant.NO : (short) (Math.abs(id_agreed) == Constant.YES ? Constant.YES : Constant.NO);
	}

	public void setId_agreed(Short id_agreed) {
		this.id_agreed = id_agreed;
	}

	public Long getB2b_qu_id() {
		return b2b_qu_id;
	}

	public void setB2b_qu_id(Long b2b_qu_id) {
		this.b2b_qu_id = b2b_qu_id;
	}

}
