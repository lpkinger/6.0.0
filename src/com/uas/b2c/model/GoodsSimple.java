package com.uas.b2c.model;

import java.util.Date;
import java.util.List;

/**
 * 上架商品的简单信息
 * 
 *
 */
public class GoodsSimple {
	
	/**
	 * 批次号
	 */
	private String batchCode;
	
	/**
	 * 交期(天)
	 */
	private Short deliveryTime;
	
	/**
	 * 产品生产日期
	 */
	private Date produceDate;
	
	/**
	 * 创建日期
	 */
	private Date createdDate;
	
	/**
	 * 库存
	 */
	private Double reserve;
	
	/**
	 * 最小包装量
	 */
	private Double minPackQty;


	private String uuid;
	/**
	 * 发布备注
	 */
	private String remark;
	
	/**
	 * 最小起订量
	 */
	private Double minBuyQty;
	
	/**
	 * 分段报价(List)
	 */
	private List<GoodsQtyPrice> prices;
	
	/**
	 * 是否原厂原装，1 原装正品，0 工厂库存
	 */
	private Short original;
	
	/**
	 * 供应商上架的样品数量（总数）
	 */
	private Double sampleQty ;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Short getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Short deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Date getProduceDate() {
		return produceDate;
	}

	public void setProduceDate(Date produceDate) {
		this.produceDate = produceDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Double getReserve() {
		return reserve;
	}

	public void setReserve(Double reserve) {
		this.reserve = reserve;
	}

	public Double getMinPackQty() {
		return minPackQty;
	}

	public void setMinPackQty(Double minPackQty) {
		this.minPackQty = minPackQty;
	}
	/**
	 * 无参构造方法
	 */
	public GoodsSimple() {
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the minBuyQty
	 */
	public Double getMinBuyQty() {
		return minBuyQty;
	}

	/**
	 * @param minBuyQty the minBuyQty to set
	 */
	public void setMinBuyQty(Double minBuyQty) {
		this.minBuyQty = minBuyQty;
	}
	
	public List<GoodsQtyPrice> getPrices() {
		return prices;
	}

	public void setPrices(List<GoodsQtyPrice> prices) {
		this.prices = prices;
	}

	/**
	 * @return the sampleQty
	 */
	public Double getSampleQty() {
		return sampleQty;
	}

	/**
	 * @param sampleQty the sampleQty to set
	 */
	public void setSampleQty(Double sampleQty) {
		this.sampleQty = sampleQty;
	}

	/**
	 * @return the original
	 */
	public Short getOriginal() {
		return original;
	}

	/**
	 * @param original the original to set
	 */
	public void setOriginal(Short original) {
		this.original = original;
	}
	
}
