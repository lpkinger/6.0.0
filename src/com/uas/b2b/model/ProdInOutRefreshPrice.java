package com.uas.b2b.model;

import com.uas.erp.core.support.KeyEntity;

public class ProdInOutRefreshPrice extends KeyEntity {

	/**
	 * 明细行序号
	 */
	private Short pbu_pdno;

	/**
	 * 单据编号
	 */
	private String pbu_inoutno; 
	
	/**
	 * 采购成本
	 */
	private Double pbu_orderprice;
	
	/**
	 * 税率
	 */
	private Double pbu_taxrate;
	
	/**
	 *  erpid
	 */
	private Long pbu_id;
	
	/**
	 * 物料编号
	 */
	private String pd_prodcode;
	
	/**
	 * 订单序号
	 */
	private Short pd_orderdetno;

	public Short getPbu_pdno() {
		return pbu_pdno;
	}

	public void setPbu_pdno(Short pbu_pdno) {
		this.pbu_pdno = pbu_pdno;
	}

	public String getPbu_inoutno() {
		return pbu_inoutno;
	}

	public void setPbu_inoutno(String pbu_inoutno) {
		this.pbu_inoutno = pbu_inoutno;
	}

	public Double getPbu_orderprice() {
		return pbu_orderprice;
	}

	public void setPbu_orderprice(Double pbu_orderprice) {
		this.pbu_orderprice = pbu_orderprice;
	}

	public Long getPbu_id() {
		return pbu_id;
	}

	public void setPbu_id(Long pbu_id) {
		this.pbu_id = pbu_id;
	}

	public Double getPbu_taxrate() {
		return pbu_taxrate;
	}

	public void setPbu_taxrate(Double pbu_taxrate) {
		this.pbu_taxrate = pbu_taxrate;
	}

	public String getPd_prodcode() {
		return pd_prodcode;
	}

	public void setPd_prodcode(String pd_prodcode) {
		this.pd_prodcode = pd_prodcode;
	}

	public Short getPd_orderdetno() {
		return pd_orderdetno;
	}

	public void setPd_orderdetno(Short pd_orderdetno) {
		this.pd_orderdetno = pd_orderdetno;
	}

	@Override
	public Object getKey() {
		return this.pbu_id;
	}

}
