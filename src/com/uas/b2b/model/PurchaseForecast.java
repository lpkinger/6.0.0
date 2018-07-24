package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.support.KeyEntity;

/**
 * ERP系统的采购预测订单
 * 
 * @author yingp
 * 
 */
public class PurchaseForecast extends KeyEntity {
	
	private Long pf_id;
	private String pf_code;
	private Date pf_date;
	private String pf_kind;
	private String pf_recorder;//录入员名字
	private String pf_purpose;//用途
	private Long em_uu;//采购员uu号
	private List<PurchaseForecastDetail> purchaseForecastDetails;
	
	public Long getPf_id() {
		return pf_id;
	}
	public void setPf_id(Long pf_id) {
		this.pf_id = pf_id;
	}
	public String getPf_code() {
		return pf_code;
	}
	public void setPf_code(String pf_code) {
		this.pf_code = pf_code;
	}
	public Date getPf_date() {
		return pf_date;
	}
	public void setPf_date(Date pf_date) {
		this.pf_date = pf_date;
	}	
	public String getPf_kind() {
		return pf_kind;
	}
	public void setPf_kind(String pf_kind) {
		this.pf_kind = pf_kind;
	}
	public String getPf_recorder() {
		return pf_recorder;
	}
	public void setPf_recorder(String pf_recorder) {
		this.pf_recorder = pf_recorder;
	}
	public String getPf_purpose() {
		return pf_purpose;
	}
	public void setPf_purpose(String pf_purpose) {
		this.pf_purpose = pf_purpose;
	}
	public List<PurchaseForecastDetail> getPurchaseForecastDetails() {
		return purchaseForecastDetails;
	}
	public void setPurchaseForecastDetails(
			List<PurchaseForecastDetail> purchaseForecastDetails) {
		this.purchaseForecastDetails = purchaseForecastDetails;
	}
	
	public Long getEm_uu() {
		return em_uu;
	}
	public void setEm_uu(Long em_uu) {
		this.em_uu = em_uu;
	}
	
	@JsonIgnore
	@Override
	public Object getKey() {
		return this.pf_id;
	}
}
