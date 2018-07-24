package com.uas.b2b.model;

public class PublicRelay {

	/**
	 * （买家或卖家定义的）分段数量
	 */
	private Double lapQty;

	/**
	 * （卖家报的）单价
	 */
	private Double price;

	public Double getLapQty() {
		return lapQty;
	}

	public void setLapQty(Double lapQty) {
		this.lapQty = lapQty;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
