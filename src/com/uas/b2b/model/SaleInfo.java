package com.uas.b2b.model;

public class SaleInfo {

	/**
	 * 销售单单据id
	 */
	private Long sa_id;

	/**
	 * 采购单号
	 */
	private String sa_code;

	public Long getSa_id() {
		return sa_id;
	}

	public void setSa_id(Long sa_id) {
		this.sa_id = sa_id;
	}

	public String getSa_code() {
		return sa_code;
	}

	public void setSa_code(String sa_code) {
		this.sa_code = sa_code;
	}
}
