package com.uas.b2b.model;

/**
 * 买家ERP系统的采购询价单明细的分段报价明细
 * 
 * @author yingp
 * 
 */
public class InquiryDetailDet {

	private Long b2b_id_id;
	private Long idd_idid;
	private Double idd_lapqty;
	private Double idd_price;

	public Long getIdd_idid() {
		return idd_idid;
	}

	public void setIdd_idid(Long idd_idid) {
		this.idd_idid = idd_idid;
	}

	public Double getIdd_lapqty() {
		return idd_lapqty;
	}

	public void setIdd_lapqty(Double idd_lapqty) {
		this.idd_lapqty = idd_lapqty;
	}

	public Double getIdd_price() {
		return idd_price;
	}

	public void setIdd_price(Double idd_price) {
		this.idd_price = idd_price;
	}

	public Long getB2b_id_id() {
		return b2b_id_id;
	}

	public void setB2b_id_id(Long b2b_id_id) {
		this.b2b_id_id = b2b_id_id;
	}

	/**
	 * 主动报价SQL封装
	 * 
	 * @param foreignKey
	 * @return
	 */
	public String toSqlString(int foreignKey) {
		return "insert into InquiryDetailDet(idd_id,idd_idid,idd_lapqty,idd_price) values (InquiryDetailDet_SEQ.nextval," + foreignKey
				+ "," + this.idd_lapqty + "," + this.idd_price + ")";
	}

}
