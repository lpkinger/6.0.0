package com.uas.b2b.model;

/**
 * 更新供应商rate
 * 
 * @author hejq
 * @time 创建时间：2017年3月27日
 */
public class VendorRate {

	/**
	 * 供应商uu
	 */
	private Long venduu;

	/**
	 * 客户uu
	 */
	private Long custuu;

	/**
	 * 费率
	 */
	private Double rate;


	public Long getVenduu() {
		return venduu;
	}

	public void setVenduu(Long venduu) {
		this.venduu = venduu;
	}

	public Long getCustuu() {
		return custuu;
	}

	public void setCustuu(Long custuu) {
		this.custuu = custuu;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

}