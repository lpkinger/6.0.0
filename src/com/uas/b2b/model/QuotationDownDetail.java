package com.uas.b2b.model;

import com.uas.erp.core.NumberUtil;

public class QuotationDownDetail {

	private Double qd_lapqty;
	private Double qd_price;

	public Double getQd_lapqty() {
		return qd_lapqty;
	}

	public void setQd_lapqty(Double qd_lapqty) {
		this.qd_lapqty = qd_lapqty;
	}

	public Double getQd_price() {
		return qd_price;
	}

	public void setQd_price(Double qd_price) {
		this.qd_price = qd_price;
	}

	public String toSqlString(int foreignKey) {
		return "insert into QuotationDownDetail(qd_id,qd_quid,qd_lapqty,qd_price) values (QuotationDownDetail_SEQ.nextval," + foreignKey
				+ "," + NumberUtil.nvl(qd_lapqty, 0) + "," + NumberUtil.nvl(qd_price, 0) + ")";
	}

}
