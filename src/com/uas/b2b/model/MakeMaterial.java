package com.uas.b2b.model;

/**
 * ERP系统的委外加工单用料明细
 * 
 * @author suntg
 * 
 */
public class MakeMaterial{

	private Short mm_detno;//行号
	private String mm_prodcode;//用料物料编号
	private Double mm_oneuseqty;//单位用量
	private Double mm_qty;//订单需求
	private Short mm_ifrep;//是否替代
	private String mm_repprodcode;//替代物料编号
	
	public Short getMm_detno() {
		return mm_detno;
	}
	public void setMm_detno(Short mm_detno) {
		this.mm_detno = mm_detno;
	}
	public String getMm_prodcode() {
		return mm_prodcode;
	}
	public void setMm_prodcode(String mm_prodcode) {
		this.mm_prodcode = mm_prodcode;
	}
	public Double getMm_oneuseqty() {
		return mm_oneuseqty;
	}
	public void setMm_oneuseqty(Double mm_oneuseqty) {
		this.mm_oneuseqty = mm_oneuseqty;
	}
	public Double getMm_qty() {
		return mm_qty;
	}
	public void setMm_qty(Double mm_qty) {
		this.mm_qty = mm_qty;
	}
	public Short getMm_ifrep() {
		return mm_ifrep;
	}
	public void setMm_ifrep(Short mm_ifrep) {
		this.mm_ifrep = mm_ifrep;
	}
	public String getMm_repprodcode() {
		return mm_repprodcode;
	}
	public void setMm_repprodcode(String mm_repprodcode) {
		this.mm_repprodcode = mm_repprodcode;
	}
	
	
	
	

}
