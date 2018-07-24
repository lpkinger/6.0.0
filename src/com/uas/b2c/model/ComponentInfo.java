package com.uas.b2c.model;


/**
 * 标准电子元器件信息
 * 
 * @author ChenHao
 *
 */
public class ComponentInfo {

	/**
	 * 器件的uuid
	 */
	private String uuid;

	/**
	 * 原厂型号
	 */
	private String code;

	/**
	 * 器件规格
	 */
	private String spec;

	/**
	 * 器件的标准单位
	 */
	private String unit;

	/**
	 * 单重（g）
	 */
	private Float weight;

	/**
	 * 图片path
	 */
	private String img;

	/**
	 * 以下为器件的库存交易属性，由器件对应的上架商品发生变化时，更新反应到器件
	 */

	/**
	 * 器件的库存
	 */
	private Double reserve;

	/**
	 * 器件的库存类型
	 */
	private Short reserveType;

	/**
	 * 器件的最低单价
	 */
	private Double minPrice;

	/**
	 * 器件的最小起订量
	 */
	private Double minBuyQty;

	/**
	 * 器件最小送货周期
	 */
	private Short minDelivery;

	/**
	 * 器件最大送货周期
	 */
	private Short maxDelivery;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getReserve() {
		return reserve;
	}

	public void setReserve(Double reserve) {
		this.reserve = reserve;
	}

	public Short getReserveType() {
		return reserveType;
	}

	public void setReserveType(Short reserveType) {
		this.reserveType = reserveType;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMinBuyQty() {
		return minBuyQty;
	}

	public void setMinBuyQty(Double minBuyQty) {
		this.minBuyQty = minBuyQty;
	}

	public Short getMinDelivery() {
		return minDelivery;
	}

	public void setMinDelivery(Short minDelivery) {
		this.minDelivery = minDelivery;
	}

	public Short getMaxDelivery() {
		return maxDelivery;
	}

	public void setMaxDelivery(Short maxDelivery) {
		this.maxDelivery = maxDelivery;
	}

}
