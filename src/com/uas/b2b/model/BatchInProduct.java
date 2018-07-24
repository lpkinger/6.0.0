package com.uas.b2b.model;

/**
 * 公共询价物料
 * 
 * @author hejq
 * @time 创建时间：2017年9月18日
 */
public class BatchInProduct {

	/**
	 * 明细id
	 */
	private Long bip_id;
	
	/**
	 * 主表id
	 */
	private Long bip_biid;
	
	/**
	 * 序号
	 */
	private Short bip_detno;
	
	/**
	 * 物料编号
	 */
	private String bip_prodcode;
	
	/**
	 * 备注
	 */
	private String bip_remark;

	/**
	 * 币别
	 */
	private String bip_currency;
	
	public Long getBip_id() {
		return bip_id;
	}

	public void setBip_id(Long bip_id) {
		this.bip_id = bip_id;
	}

	public Long getBip_biid() {
		return bip_biid;
	}

	public void setBip_biid(Long bip_biid) {
		this.bip_biid = bip_biid;
	}

	public Short getBip_detno() {
		return bip_detno;
	}

	public void setBip_detno(Short bip_detno) {
		this.bip_detno = bip_detno;
	}

	public String getBip_prodcode() {
		return bip_prodcode;
	}

	public void setBip_prodcode(String bip_prodcode) {
		this.bip_prodcode = bip_prodcode;
	}

	public String getBip_remark() {
		return bip_remark;
	}

	public void setBip_remark(String bip_remark) {
		this.bip_remark = bip_remark;
	}

	public String getBip_currency() {
		return bip_currency;
	}

	public void setBip_currency(String bip_currency) {
		this.bip_currency = bip_currency;
	}
	
}
