package com.uas.b2b.model;


public class APCheckDetail {
	
	private Integer ad_detno; // 明细行号
	private Long ad_prid;// 客户物料ID #
	private String ad_inoutno; // 客户出入库单号   ad_inoutno 出入库单号
	private String ad_orderclass; // 客户出入库类型 #   
	private Long ad_orderdetno; // 客户出入库序号 # 
	private Double ad_price; // 单价
	private Double ad_b2bqty; // 对账数量
	private Double ad_b2bamount; // 本次对账金额
	private Double ad_custcheckqty; // 客户确认数量 #
	private String ad_remark; // 备注 
	private Long ad_id;// id #
	private Long ad_apid; // 关联Id  #
	private Integer ad_status; // 对账状态 #
	
	public Integer getAd_detno() {
		return ad_detno;
	}

	public void setAd_detno(Integer ad_detno) {
		this.ad_detno = ad_detno;
	}

	public Long getAd_prid() {
		return ad_prid;
	}

	public void setAd_prid(Long ad_prid) {
		this.ad_prid = ad_prid;
	}

	public String getAd_inoutno() {
		return ad_inoutno;
	}

	public void setAd_inoutno(String ad_inoutno) {
		this.ad_inoutno = ad_inoutno;
	}

	public String getAd_orderclass() {
		return ad_orderclass;
	}

	public void setAd_orderclass(String ad_orderclass) {
		this.ad_orderclass = ad_orderclass;
	}

	public Long getAd_orderdetno() {
		return ad_orderdetno;
	}

	public void setAd_orderdetno(Long ad_orderdetno) {
		this.ad_orderdetno = ad_orderdetno;
	}

	public Double getAd_price() {
		return ad_price;
	}

	public void setAd_price(Double ad_price) {
		this.ad_price = ad_price;
	}

	public Double getAd_b2bqty() {
		return ad_b2bqty;
	}

	public void setAd_b2bqty(Double ad_b2bqty) {
		this.ad_b2bqty = ad_b2bqty;
	}

	public Double getAd_b2bamount() {
		return ad_b2bamount;
	}

	public void setAd_b2bamount(Double ad_b2bamount) {
		this.ad_b2bamount = ad_b2bamount;
	}

	public Double getAd_custcheckqty() {
		return ad_custcheckqty;
	}

	public void setAd_custcheckqty(Double ad_custcheckqty) {
		this.ad_custcheckqty = ad_custcheckqty;
	}

	public String getAd_remark() {
		return ad_remark;
	}

	public void setAd_remark(String ad_remark) {
		this.ad_remark = ad_remark;
	}

	public Long getAd_id() {
		return ad_id;
	}

	public void setAd_id(Long ad_id) {
		this.ad_id = ad_id;
	}

	public Long getAd_apid() {
		return ad_apid;
	}

	public void setAd_apid(Long ad_apid) {
		this.ad_apid = ad_apid;
	}

	public Integer getAd_status() {
		return ad_status;
	}

	public void setAd_status(Integer ad_status) {
		this.ad_status = ad_status;
	}

	public String toSqlString(int primaryKey, String orderClass) {
		return "insert into apcheckdetail(ad_id,ad_acid,ad_detno,ad_inoutno,ad_sourcecode,ad_sourcedetno,ad_sourcetype,ad_price,ad_b2bqty,ad_b2bamount)"
				+ " values ( " + "APCheckdetail_seq.nextval," + primaryKey + ", " + ad_detno + ",'" + ad_inoutno
				+ "', '" + ad_inoutno + "', " + ad_orderdetno + ",'" + orderClass + "', " + ad_price + ", " + ad_b2bqty
				+ ", " + ad_b2bamount + ")";
	}
	
}
