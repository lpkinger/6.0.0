package com.uas.b2b.model;

/**
 * 卖家ERP系统的已结案客户送货提醒单
 * 
 * @author yingp
 * 
 */
public class SaleNotifyDownEnd {

	private long b2b_pn_id;
	private long cu_uu;

	public long getB2b_pn_id() {
		return b2b_pn_id;
	}

	public void setB2b_pn_id(long b2b_pn_id) {
		this.b2b_pn_id = b2b_pn_id;
	}

	public long getCu_uu() {
		return cu_uu;
	}

	public void setCu_uu(long cu_uu) {
		this.cu_uu = cu_uu;
	}

}
