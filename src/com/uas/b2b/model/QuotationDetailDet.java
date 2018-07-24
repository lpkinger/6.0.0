package com.uas.b2b.model;

public class QuotationDetailDet {
	private Long qdd_qdid;
	private Double qdd_lapqty;
	private Double qdd_price;

	public Long getQdd_qdid() {
		return qdd_qdid;
	}

	public void setQdd_qdid(Long qdd_qdid) {
		this.qdd_qdid = qdd_qdid;
	}

	public Double getQdd_lapqty() {
		return qdd_lapqty;
	}

	public void setQdd_lapqty(Double qdd_lapqty) {
		this.qdd_lapqty = qdd_lapqty;
	}

	public Double getQdd_price() {
		return qdd_price;
	}

	public void setQdd_price(Double qdd_price) {
		this.qdd_price = qdd_price;
	}

	public String toSqlString(int foreignKey) {
		return "insert into quotationdetaildet (qdd_id, qdd_qdid, qdd_lapqty, qdd_price) "
				+ "values (QUOTATIONDETAILDET_seq.nextval, "
				+ foreignKey
				+ ", "
				+ this.qdd_lapqty
				+ ", "
				+ this.qdd_price
				+ ")";
	}
}
