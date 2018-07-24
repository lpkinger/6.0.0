package com.uas.b2c.model;

/**
 * 商品分段数量价格
 * 
 * @author suntg
 * @since 2016年1月28日下午8:01:23 新建
 */
public class GoodsQtyPrice {

	/**
	 * 起始数量
	 */
	private Double start;
	/**
	 * 截止数量
	 */
	private Double end;
	/**
	 * 对应价格，单位为元
	 */
	private Double price;

	public Double getStart() {
		return start;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getEnd() {
		return end;
	}

	public void setEnd(Double end) {
		this.end = end;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
