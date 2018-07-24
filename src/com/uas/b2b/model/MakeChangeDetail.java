package com.uas.b2b.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

public class MakeChangeDetail {

	private Long md_id;// id
	private Long md_mcid;// 主记录id
	private Short md_detno;// 序号
	private String md_makecode;// 委外加工单号
	private Double md_oldqty;// 原需求数量
	private Double md_newqty;// 新需求数量
	private Double md_oldprice;// 原加工单价
	private Double md_newprice;// 新加工单价
	private Date md_planbegindate;// 原计划开工日期
	private Date md_planenddate;// 原计划完工日期
	private Date md_newplanbegindate;// 新计划开工日期
	private Date md_newplanenddate;// 新计划完工日期
	private String md_remark;// 备注
	private String md_reason;// 变更原因
	private Float md_taxrate;// 原税率
	private Float md_newtaxrate;// 新税率
	@JsonIgnore
	public Long getMd_id() {
		return md_id;
	}
	public void setMd_id(Long md_id) {
		this.md_id = md_id;
	}
	public Long getMd_mcid() {
		return md_mcid;
	}
	public void setMd_mcid(Long md_mcid) {
		this.md_mcid = md_mcid;
	}
	public Short getMd_detno() {
		return md_detno;
	}
	public void setMd_detno(Short md_detno) {
		this.md_detno = md_detno;
	}
	public String getMd_makecode() {
		return md_makecode;
	}
	public void setMd_makecode(String md_makecode) {
		this.md_makecode = md_makecode;
	}
	public Double getMd_oldqty() {
		return md_oldqty;
	}
	public void setMd_oldqty(Double md_oldqty) {
		this.md_oldqty = md_oldqty;
	}
	public Double getMd_newqty() {
		return md_newqty;
	}
	public void setMd_newqty(Double md_newqty) {
		this.md_newqty = md_newqty;
	}
	public Double getMd_oldprice() {
		return md_oldprice;
	}
	public void setMd_oldprice(Double md_oldprice) {
		this.md_oldprice = md_oldprice;
	}
	public Double getMd_newprice() {
		return md_newprice;
	}
	public void setMd_newprice(Double md_newprice) {
		this.md_newprice = md_newprice;
	}
	public Date getMd_planbegindate() {
		return md_planbegindate;
	}
	public void setMd_planbegindate(Date md_planbegindate) {
		this.md_planbegindate = md_planbegindate;
	}
	public Date getMd_planenddate() {
		return md_planenddate;
	}
	public void setMd_planenddate(Date md_planenddate) {
		this.md_planenddate = md_planenddate;
	}
	public Date getMd_newplanbegindate() {
		return md_newplanbegindate;
	}
	public void setMd_newplanbegindate(Date md_newplanbegindate) {
		this.md_newplanbegindate = md_newplanbegindate;
	}
	public Date getMd_newplanenddate() {
		return md_newplanenddate;
	}
	public void setMd_newplanenddate(Date md_newplanenddate) {
		this.md_newplanenddate = md_newplanenddate;
	}
	public String getMd_remark() {
		return md_remark;
	}
	public void setMd_remark(String md_remark) {
		this.md_remark = md_remark;
	}
	public String getMd_reason() {
		return md_reason;
	}
	public void setMd_reason(String md_reason) {
		this.md_reason = md_reason;
	}
	public Float getMd_taxrate() {
		return md_taxrate;
	}
	public void setMd_taxrate(Float md_taxrate) {
		this.md_taxrate = md_taxrate;
	}
	public Float getMd_newtaxrate() {
		return md_newtaxrate;
	}
	public void setMd_newtaxrate(Float md_newtaxrate) {
		this.md_newtaxrate = md_newtaxrate;
	}
	
}
