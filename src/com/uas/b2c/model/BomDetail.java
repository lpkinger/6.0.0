package com.uas.b2c.model;

import java.util.Date;

public class BomDetail {
	/**
	 * 物料编号
	 */
	private String productCode;

	/**
	 * 物料名称
	 */
	private String productTitle;

	/**
	 * 型号
	  */
	private String code;

	/**
	 * 品牌
	 */
	private String brand;

	/**
	 * 原产型号
	 */
	private String cmpCode;

	/**
	 * 原产品牌
	 */
	private String inbrand;

	/**
	 * 规格描述
	  */
	private String spec;

	/**
	 * 单位用量
	 */
	private Integer unitConsumption;

	/**
	 * UAS预估单价
	 */
	private Double uasEstimatePrice;

	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 币种
	 */
	private String currency;

	/**
	 * 税率
	 */
	private Double taxRate;

	/**
	 * 含税单价
	 */
	private Double taxUnitPrice;

	/**
	 * 供应商UU
	  */
	private Long vendUU;

	/**
	 * 供应商名称
	 */
	private String vendName;

	/**
	 * 核价后价格
	  */
	private Double pricingPrice;

	/**
	 * 当前价格在价格库的编号
	 */
	private Long pid;

	/**
	 * 核价时间
	 */
	private Date pricingTime;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCmpCode() {
		return cmpCode;
	}

	public void setCmpCode(String cmpCode) {
		this.cmpCode = cmpCode;
	}

	public String getInbrand() {
		return inbrand;
	}

	public void setInbrand(String inbrand) {
		this.inbrand = inbrand;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Integer getUnitConsumption() {
		return unitConsumption;
	}

	public void setUnitConsumption(Integer unitConsumption) {
		this.unitConsumption = unitConsumption;
	}

	public Double getUasEstimatePrice() {
		return uasEstimatePrice;
	}

	public void setUasEstimatePrice(Double uasEstimatePrice) {
		this.uasEstimatePrice = uasEstimatePrice;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getTaxUnitPrice() {
		return taxUnitPrice;
	}

	public void setTaxUnitPrice(Double taxUnitPrice) {
		this.taxUnitPrice = taxUnitPrice;
	}

	public Long getVendUU() {
		return vendUU;
	}

	public void setVendUU(Long vendUU) {
		this.vendUU = vendUU;
	}

	public String getVendName() {
		return vendName;
	}

	public void setVendName(String vendName) {
		this.vendName = vendName;
	}

	public Double getPricingPrice() {
		return pricingPrice;
	}

	public void setPricingPrice(Double pricingPrice) {
		this.pricingPrice = pricingPrice;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Date getPricingTime() {
		return pricingTime;
	}

	public void setPricingTime(Date pricingTime) {
		this.pricingTime = pricingTime;
	}

	@Override
	public String toString() {
		return "BomDetail [productCode=" + productCode + ", productTitle=" + productTitle + ", code=" + code
				+ ", brand=" + brand + ", cmpCode=" + cmpCode + ", inbrand=" + inbrand + ", spec=" + spec
				+ ", unitConsumption=" + unitConsumption + ", uasEstimatePrice=" + uasEstimatePrice + ", unit=" + unit
				+ ", currency=" + currency + ", taxRate=" + taxRate + ", taxUnitPrice=" + taxUnitPrice + ", vendUU="
				+ vendUU + ", vendName=" + vendName + ", pricingPrice=" + pricingPrice + ", pid=" + pid
				+ ", pricingTime=" + pricingTime + "]";
	}
	
}
