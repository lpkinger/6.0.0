package com.uas.b2b.model;

import java.sql.Date;

import com.uas.erp.core.support.KeyEntity;

/**
 * ERP系统的委外加工单
 * 
 * @author suntg
 * 
 */
public class Make extends KeyEntity{

	private Long ma_id;//id
	private String ma_code;//委外编号
	private Date ma_date;//单据日期
	private String ma_tasktype;//单据类型
	private Date ma_requiredate;//需求日期
	private Long ve_uu;//供应商UU号
	private String ma_vendname;//供应商名称
	private String ma_prodcode;//物料编号
	private Double ma_qty;//需求数量
	private Double ma_makeqty;//已生产数
	private String ma_kind;//工单类型
	private Double ma_price;//加工单价
	private Double ma_total;//加工金额
	private String ma_currency;//币别
	private Float ma_rate;//汇率
	private Double ma_taxrate;//税率
	private String ma_shipaddresscode;//收货地址
	private String ma_payments;//付款方式
	private String ma_recorder;//录入人
	private Long ma_recorderid;//录入人ID
	private Date ma_planbegindate;//计划开工日期
	private Date ma_planenddate;//计划完工日期
	private String ma_auditman;//审核人
	private String ma_remark;//备注
	private String ma_factory;
//	private List<MakeMaterial> materials;//用料明细
	
	public String getMa_factory() {
		return ma_factory;
	}
	
	public void setMa_factory(String ma_factory) {
		this.ma_factory = ma_factory;
	}

	public Long getMa_id() {
		return ma_id;
	}

	public void setMa_id(Long ma_id) {
		this.ma_id = ma_id;
	}

	public String getMa_code() {
		return ma_code;
	}

	public void setMa_code(String ma_code) {
		this.ma_code = ma_code;
	}

	public Date getMa_date() {
		return ma_date;
	}

	public void setMa_date(Date ma_date) {
		this.ma_date = ma_date;
	}

	public String getMa_tasktype() {
		return ma_tasktype;
	}

	public void setMa_tasktype(String ma_tasktype) {
		this.ma_tasktype = ma_tasktype;
	}

	public Date getMa_requiredate() {
		return ma_requiredate;
	}

	public void setMa_requiredate(Date ma_requiredate) {
		this.ma_requiredate = ma_requiredate;
	}

	public Long getVe_uu() {
		return ve_uu;
	}

	public void setVe_uu(Long ve_uu) {
		this.ve_uu = ve_uu;
	}

	public String getMa_prodcode() {
		return ma_prodcode;
	}

	public void setMa_prodcode(String ma_prodcode) {
		this.ma_prodcode = ma_prodcode;
	}

	public Double getMa_qty() {
		return ma_qty;
	}

	public void setMa_qty(Double ma_qty) {
		this.ma_qty = ma_qty;
	}

	public Double getMa_makeqty() {
		return ma_makeqty;
	}

	public void setMa_makeqty(Double ma_makeqty) {
		this.ma_makeqty = ma_makeqty;
	}

	public String getMa_kind() {
		return ma_kind;
	}

	public void setMa_kind(String ma_kind) {
		this.ma_kind = ma_kind;
	}

	public Double getMa_price() {
		return ma_price;
	}

	public void setMa_price(Double ma_price) {
		this.ma_price = ma_price;
	}

	public Double getMa_total() {
		return ma_total;
	}

	public void setMa_total(Double ma_total) {
		this.ma_total = ma_total;
	}

	public String getMa_currency() {
		return ma_currency;
	}

	public void setMa_currency(String ma_currency) {
		this.ma_currency = ma_currency;
	}

	public Float getMa_rate() {
		return ma_rate;
	}

	public void setMa_rate(Float ma_rate) {
		this.ma_rate = ma_rate;
	}

	public Double getMa_taxrate() {
		return ma_taxrate;
	}

	public void setMa_taxrate(Double ma_taxrate) {
		this.ma_taxrate = ma_taxrate;
	}

	public String getMa_shipaddresscode() {
		return ma_shipaddresscode;
	}

	public void setMa_shipaddresscode(String ma_shipaddresscode) {
		this.ma_shipaddresscode = ma_shipaddresscode;
	}

	public String getMa_payments() {
		return ma_payments;
	}

	public void setMa_payments(String ma_payments) {
		this.ma_payments = ma_payments;
	}

	public String getMa_recorder() {
		return ma_recorder;
	}

	public void setMa_recorder(String ma_recorder) {
		this.ma_recorder = ma_recorder;
	}

	public Date getMa_planbegindate() {
		return ma_planbegindate;
	}

	public void setMa_planbegindate(Date ma_planbegindate) {
		this.ma_planbegindate = ma_planbegindate;
	}

	public Date getMa_planenddate() {
		return ma_planenddate;
	}

	public void setMa_planenddate(Date ma_planenddate) {
		this.ma_planenddate = ma_planenddate;
	}

	public String getMa_auditman() {
		return ma_auditman;
	}

	public void setMa_auditman(String ma_auditman) {
		this.ma_auditman = ma_auditman;
	}

	public String getMa_remark() {
		return ma_remark;
	}

	public void setMa_remark(String ma_remark) {
		this.ma_remark = ma_remark;
	}

//	public List<MakeMaterial> getMaterials() {
//		return materials;
//	}
//
//	public void setMaterials(List<MakeMaterial> materials) {
//		this.materials = materials;
//	}

	public Long getMa_recorderid() {
		return ma_recorderid;
	}

	public void setMa_recorderid(Long ma_recorderid) {
		this.ma_recorderid = ma_recorderid;
	}

	public String getMa_vendname() {
		return ma_vendname;
	}

	public void setMa_vendname(String ma_vendname) {
		this.ma_vendname = ma_vendname;
	}

	@Override
	public Object getKey() {
		return this.ma_id;
	}

}
