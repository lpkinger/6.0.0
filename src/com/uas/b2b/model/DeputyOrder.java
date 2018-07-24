package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;

public class DeputyOrder  extends KeyEntity {
	/**
	 * 主键id
	 */
	private Long id;

	/********************
	 * 基本信息
	 ********************/

	/**
	 * 流水号
	 */
	private String code;

	/**
	 * 状态
	 */
	private String status;

	/**
	 * 状态码
	 */
	private Integer statuscode;

	/**
	 * 业务状态
	 */
	private String prostatus;

	/**
	 * 业务状态码
	 */
	private Integer prostatuscode;

	/**
	 * 录入状态
	 */
	private String entrystatus;

	/**
	 * 录入日期
	 */
	private Date entrydate;

	/**
	 * 主协议签订日期
	 */
	private Date madealdate;

	/**
	 * 主协议编号
	 */
	private String madealcode;

	/**
	 * 终端供应商名称
	 */
	private String tervendor;

	/**
	 * 终端供应商uu
	 */
	private Long tervenduu;

	/**
	 * 供应商联系电话
	 */
	private String vendtel;

	/**
	 * 供应商联系人
	 */
	private String venduser;

	/**
	 * 联系人uu
	 */
	private Long venduseruu;

	/**
	 * 发货时间
	 */
	private Date shipdate;

	/**
	 * 货物包装
	 */
	private String goodspacking;

	/**
	 * 供应商交货方式
	 */
	private String deliverymethod;

	/**
	 * 我方提货方式
	 */
	private String pickupmethod;

	/**
	 * 付款方式
	 */
	private String paymentmethod;

	/**
	 * 币别
	 */
	private String currency;

	/**
	 * 费率
	 */
	private Double rate;

	/*******************
	 * 下单明细
	 *******************/

	/**
	 * 采购变更单明细
	 */
	private List<DeputyOrderItem> deputyOrderItems;

	/********************
	 * 采购订单
	 ********************/

	/**
	 * 采购订单
	 */
	private String salecode;

	/**
	 * 总金额
	 */
	private Double totalamount;

	/**
	 * 我方付款提货时间
	 */
	private Date paydeldate;

	/**
	 * 付款总额
	 */
	private Double totalpayament;

	/**
	 * 付款时间
	 */
	private Date paymentdate;

	/**
	 * 风险承担方式
	 */
	private String riskmethod;

	/**
	 * 运、税费承担方式
	 */
	private String taxpaymentmethod;

	/*******************
	 * 付款确认书
	 *******************/

	/**
	 * 终端供应商地址
	 */
	private String tervendaddress;

	/**
	 * 公司全称
	 */
	private String companyname;

	/**
	 * 要求付款日期
	 */
	private Date requirepaydate;

	/**
	 * 实际付款日期
	 */
	private Date actualpaydate;

	/**
	 * 付款金额
	 */
	private Double usdpayment;

	/**
	 * 实际付款金额（usd）
	 */
	private Double actusdpayment;

	/**
	 * 银行名称
	 */
	private String bankname;

	/**
	 * 银行账户
	 */
	private String bankaccount;

	/**
	 * 银行地址
	 */
	private String bankaddress;

	/**
	 * 银行代码
	 */
	private String bankcode;

	/**
	 * 其他资料
	 */
	private String otherdata;

	/*****************
	 * 买卖合同
	 *****************/

	/**
	 * 合同编号
	 */
	private String salepocode;

	/**
	 * 我方法定代表人
	 */
	private String legalrepresent;

	/**
	 * 我方联系电话
	 */
	private String usertel;

	/**
	 * 我方联系地址
	 */
	private String enaddress;

	/**
	 * 我方传真
	 */
	private String enfax;

	/**
	 * 客户名称
	 */
	private String customer;

	/**
	 * 货款支付方式
	 */
	private String paymethod;

	/**
	 * 货款支付时间
	 */
	private Date paydate;

	/**
	 * 交货时间
	 */
	private Date deliverydate;

	/**
	 * 录入企业uu
	 */
	private Long enuu;

	/**
	 * 付款公司名称
	 */
	private String paycomname;

	/**
	 * 付款公司地址
	 */
	private String paycomaddress;
	
	/**
	 * 审核意见
	 */
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(Integer statuscode) {
		this.statuscode = statuscode;
	}

	public String getProstatus() {
		return prostatus;
	}

	public void setProstatus(String prostatus) {
		this.prostatus = prostatus;
	}

	public Integer getProstatuscode() {
		return prostatuscode;
	}

	public void setProstatuscode(Integer prostatuscode) {
		this.prostatuscode = prostatuscode;
	}

	public String getEntrystatus() {
		return entrystatus;
	}

	public void setEntrystatus(String entrystatus) {
		this.entrystatus = entrystatus;
	}

	public Date getEntrydate() {
		return entrydate;
	}

	public void setEntrydate(Date entrydate) {
		this.entrydate = entrydate;
	}

	public Date getMadealdate() {
		return madealdate;
	}

	public void setMadealdate(Date madealdate) {
		this.madealdate = madealdate;
	}

	public String getMadealcode() {
		return madealcode;
	}

	public void setMadealcode(String madealcode) {
		this.madealcode = madealcode;
	}

	public String getTervendor() {
		return tervendor;
	}

	public void setTervendor(String tervendor) {
		this.tervendor = tervendor;
	}

	public Long getTervenduu() {
		return tervenduu;
	}

	public void setTervenduu(Long tervenduu) {
		this.tervenduu = tervenduu;
	}

	public String getVendtel() {
		return vendtel;
	}

	public void setVendtel(String vendtel) {
		this.vendtel = vendtel;
	}

	public String getVenduser() {
		return venduser;
	}

	public void setVenduser(String venduser) {
		this.venduser = venduser;
	}

	public Long getVenduseruu() {
		return venduseruu;
	}

	public void setVenduseruu(Long venduseruu) {
		this.venduseruu = venduseruu;
	}

	public Date getShipdate() {
		return shipdate;
	}

	public void setShipdate(Date shipdate) {
		this.shipdate = shipdate;
	}

	public String getGoodspacking() {
		return goodspacking;
	}

	public void setGoodspacking(String goodspacking) {
		this.goodspacking = goodspacking;
	}

	public String getDeliverymethod() {
		return deliverymethod;
	}

	public void setDeliverymethod(String deliverymethod) {
		this.deliverymethod = deliverymethod;
	}

	public String getPickupmethod() {
		return pickupmethod;
	}

	public void setPickupmethod(String pickupmethod) {
		this.pickupmethod = pickupmethod;
	}

	public String getPaymentmethod() {
		return paymentmethod;
	}

	public void setPaymentmethod(String paymentmethod) {
		this.paymentmethod = paymentmethod;
	}

	public List<DeputyOrderItem> getDeputyOrderItems() {
		return deputyOrderItems;
	}

	public void setDeputyOrderItems(List<DeputyOrderItem> deputyOrderItems) {
		this.deputyOrderItems = deputyOrderItems;
	}

	public Double getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(Double totalamount) {
		this.totalamount = totalamount;
	}

	public Date getPaydeldate() {
		return paydeldate;
	}

	public void setPaydeldate(Date paydeldate) {
		this.paydeldate = paydeldate;
	}

	public Double getTotalpayament() {
		return totalpayament;
	}

	public void setTotalpayament(Double totalpayament) {
		this.totalpayament = totalpayament;
	}

	public Date getPaymentdate() {
		return paymentdate;
	}

	public void setPaymentdate(Date paymentdate) {
		this.paymentdate = paymentdate;
	}

	public String getRiskmethod() {
		return riskmethod;
	}

	public void setRiskmethod(String riskmethod) {
		this.riskmethod = riskmethod;
	}

	public String getTaxpaymentmethod() {
		return taxpaymentmethod;
	}

	public void setTaxpaymentmethod(String taxpaymentmethod) {
		this.taxpaymentmethod = taxpaymentmethod;
	}

	public String getTervendaddress() {
		return tervendaddress;
	}

	public void setTervendaddress(String tervendaddress) {
		this.tervendaddress = tervendaddress;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public Date getRequirepaydate() {
		return requirepaydate;
	}

	public void setRequirepaydate(Date requirepaydate) {
		this.requirepaydate = requirepaydate;
	}

	public Date getActualpaydate() {
		return actualpaydate;
	}

	public void setActualpaydate(Date actualpaydate) {
		this.actualpaydate = actualpaydate;
	}

	public Double getUsdpayment() {
		return usdpayment;
	}

	public void setUsdpayment(Double usdpayment) {
		this.usdpayment = usdpayment;
	}

	public Double getActusdpayment() {
		return actusdpayment;
	}

	public void setActusdpayment(Double actusdpayment) {
		this.actusdpayment = actusdpayment;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}

	public String getBankaddress() {
		return bankaddress;
	}

	public void setBankaddress(String bankaddress) {
		this.bankaddress = bankaddress;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getOtherdata() {
		return otherdata;
	}

	public void setOtherdata(String otherdata) {
		this.otherdata = otherdata;
	}

	public String getLegalrepresent() {
		return legalrepresent;
	}

	public void setLegalrepresent(String legalrepresent) {
		this.legalrepresent = legalrepresent;
	}

	public String getUsertel() {
		return usertel;
	}

	public void setUsertel(String usertel) {
		this.usertel = usertel;
	}

	public String getEnaddress() {
		return enaddress;
	}

	public void setEnaddress(String enaddress) {
		this.enaddress = enaddress;
	}

	public String getEnfax() {
		return enfax;
	}

	public void setEnfax(String enfax) {
		this.enfax = enfax;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getPaymethod() {
		return paymethod;
	}

	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}

	public Date getPaydate() {
		return paydate;
	}

	public void setPaydate(Date paydate) {
		this.paydate = paydate;
	}

	public Date getDeliverydate() {
		return deliverydate;
	}

	public void setDeliverydate(Date deliverydate) {
		this.deliverydate = deliverydate;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public String getSalecode() {
		return salecode;
	}

	public void setSalecode(String salecode) {
		this.salecode = salecode;
	}

	public String getSalepocode() {
		return salepocode;
	}

	public void setSalepocode(String salepocode) {
		this.salepocode = salepocode;
	}

	public String getPaycomname() {
		return paycomname;
	}

	public void setPaycomname(String paycomname) {
		this.paycomname = paycomname;
	}

	public String getPaycomaddress() {
		return paycomaddress;
	}

	public void setPaycomaddress(String paycomaddress) {
		this.paycomaddress = paycomaddress;
	}

	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 这边需要来源编号做查询
	 */
	@Override
	public Object getKey() {
		return this.salecode;
	}

}
