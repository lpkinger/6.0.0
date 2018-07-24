package com.uas.b2b.model;

public class DeputyOrderItem {
	/**
	 * id
	 */
	private Long id;

	/**
	 * 产品名称
	 */
	private String prodname;

	/**
	 * 品牌
	 */
	private String prodbrand;

	/**
	 * 产品型号
	 */
	private String prodcode;

	/**
	 * 产品规格
	 */
	private String prodspec;

	/**
	 * 数量
	 */
	private Integer amount;

	/**
	 * 单价
	 */
	private Double unitprice;
	
	/**
	 * 订单单价
	 */
	private Double purcprice;

	/**
	 * 总金额
	 */
	private Double totalprice;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 序号
	 */
	private Integer detno;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}

	public String getProdbrand() {
		return prodbrand;
	}

	public void setProdbrand(String prodbrand) {
		this.prodbrand = prodbrand;
	}

	public String getProdcode() {
		return prodcode;
	}

	public void setProdcode(String prodcode) {
		this.prodcode = prodcode;
	}

	public String getProdspec() {
		return prodspec;
	}

	public void setProdspec(String prodspec) {
		this.prodspec = prodspec;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Double getUnitprice() {
		return unitprice;
	}

	public void setUnitprice(Double unitprice) {
		this.unitprice = unitprice;
	}

	public Double getPurcprice() {
		return purcprice;
	}

	public void setPurcprice(Double purcprice) {
		this.purcprice = purcprice;
	}

	public Double getTotalprice() {
		return totalprice;
	}

	public void setTotalprice(Double totalprice) {
		this.totalprice = totalprice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDetno() {
		return detno;
	}

	public void setDetno(Integer detno) {
		this.detno = detno;
	}

}
