package com.uas.b2b.model;

import com.uas.erp.core.support.KeyEntity;

/**
 * ERP系统的采购订单明细（针对结案、反结案）
 * 
 * @author suntg
 * 
 */
public class MakeEnd extends KeyEntity{

	private String ma_code;
	private Long ma_id;
	private short ma_ended;
	
	public String getMa_code() {
		return ma_code;
	}
	public void setMa_code(String ma_code) {
		this.ma_code = ma_code;
	}
	public Long getMa_id() {
		return ma_id;
	}
	public void setMa_id(Long ma_id) {
		this.ma_id = ma_id;
	}
	public short getMa_ended() {
		return ma_ended;
	}
	public void setMa_ended(short ma_ended) {
		this.ma_ended = ma_ended;
	}
	
	@Override
	public Object getKey() {
		return ma_id;
	}


}
