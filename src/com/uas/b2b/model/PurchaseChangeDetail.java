package com.uas.b2b.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

public class PurchaseChangeDetail {

	private Long pcd_pcid;
	private short pcd_detno;
	private short pcd_pddetno;
	private String pcd_prodcode;
	private String pcd_newprodcode;
	private Double pcd_oldqty;
	private Double pcd_newqty;
	private Double pcd_oldprice;
	private Double pcd_newprice;
	private Date pcd_olddelivery;
	private Date pcd_newdelivery;
	private Float pcd_taxrate;
	private Float pcd_newtaxrate;
	private String pcd_remark;

	@JsonIgnore
	public Long getPcd_pcid() {
		return pcd_pcid;
	}

	public void setPcd_pcid(Long pcd_pcid) {
		this.pcd_pcid = pcd_pcid;
	}

	public short getPcd_detno() {
		return pcd_detno;
	}

	public void setPcd_detno(short pcd_detno) {
		this.pcd_detno = pcd_detno;
	}

	public short getPcd_pddetno() {
		return pcd_pddetno;
	}

	public void setPcd_pddetno(short pcd_pddetno) {
		this.pcd_pddetno = pcd_pddetno;
	}

	public String getPcd_prodcode() {
		return pcd_prodcode;
	}

	public void setPcd_prodcode(String pcd_prodcode) {
		this.pcd_prodcode = pcd_prodcode;
	}

	public String getPcd_newprodcode() {
		return pcd_newprodcode;
	}

	public void setPcd_newprodcode(String pcd_newprodcode) {
		this.pcd_newprodcode = pcd_newprodcode;
	}

	public Double getPcd_oldqty() {
		return pcd_oldqty;
	}

	public void setPcd_oldqty(Double pcd_oldqty) {
		this.pcd_oldqty = pcd_oldqty;
	}

	public Double getPcd_newqty() {
		return pcd_newqty;
	}

	public void setPcd_newqty(Double pcd_newqty) {
		this.pcd_newqty = pcd_newqty;
	}

	public Double getPcd_oldprice() {
		return pcd_oldprice;
	}

	public void setPcd_oldprice(Double pcd_oldprice) {
		this.pcd_oldprice = pcd_oldprice;
	}

	public Double getPcd_newprice() {
		return pcd_newprice;
	}

	public void setPcd_newprice(Double pcd_newprice) {
		this.pcd_newprice = pcd_newprice;
	}

	public Date getPcd_olddelivery() {
		return pcd_olddelivery;
	}

	public void setPcd_olddelivery(Date pcd_olddelivery) {
		this.pcd_olddelivery = pcd_olddelivery;
	}

	public Date getPcd_newdelivery() {
		return pcd_newdelivery;
	}

	public void setPcd_newdelivery(Date pcd_newdelivery) {
		this.pcd_newdelivery = pcd_newdelivery;
	}

	public Float getPcd_taxrate() {
		return pcd_taxrate;
	}

	public void setPcd_taxrate(Float pcd_taxrate) {
		this.pcd_taxrate = pcd_taxrate;
	}

	public Float getPcd_newtaxrate() {
		return pcd_newtaxrate;
	}

	public void setPcd_newtaxrate(Float pcd_newtaxrate) {
		this.pcd_newtaxrate = pcd_newtaxrate;
	}

	public String getPcd_remark() {
		return pcd_remark;
	}

	public void setPcd_remark(String pcd_remark) {
		this.pcd_remark = pcd_remark;
	}
}
