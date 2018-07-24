package com.uas.b2c.model;

import java.util.Date;
import java.util.List;

/**
 * 上架商品的信息
 * 
 * @author ChenHao
 *
 */
public class Goods {

	/**
	 * 对应的器件uuid
	 */
	private String uuid;

	/**
	 * UAS里面上架申请单商品明细ID
	 */
	private Long sourceId;

	/**
	 * 交期
	 */
	private Short deliveryTime;

	/**
	 * 产品生产日期
	 */
	private Date produceDate;

	/**
	 * 本批次的库存数量
	 */
	private Double reserve;

	/**
	 * 发布备注
	 */
	private String remark;

	/**
	 * 最小包装量
	 */
	private Double minPackQty;

	/**
	 * 最小起订量
	 */
	private Double minBuyQty;

	/**
	 * 分段报价(List)
	 */
	private List<GoodsQtyPrice> prices;

	/**
	 * 货物类型 Spot_Code(1051, "现货") Futures_Code(1052, "期货")
	 */
	private Integer type;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getProduceDate() {
		return produceDate;
	}

	public void setProduceDate(Date produceDate) {
		this.produceDate = produceDate;
	}

	public Double getReserve() {
		return reserve;
	}

	public void setReserve(Double reserve) {
		this.reserve = reserve;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getMinPackQty() {
		return minPackQty;
	}

	public void setMinPackQty(Double minPackQty) {
		this.minPackQty = minPackQty;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<GoodsQtyPrice> getPrices() {
		return prices;
	}

	public void setPrices(List<GoodsQtyPrice> prices) {
		this.prices = prices;
	}

	public Short getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Short deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Integer getType() {
		return type;
	}

	public Double getMinBuyQty() {
		return minBuyQty;
	}

	public void setMinBuyQty(Double minBuyQty) {
		this.minBuyQty = minBuyQty;
	}

}
