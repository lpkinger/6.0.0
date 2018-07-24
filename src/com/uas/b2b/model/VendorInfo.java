package com.uas.b2b.model;

/**
 * 更新供应商rate
 * 
 * @author hejq
 * @time 创建时间：2017年3月27日
 */
public class VendorInfo {

	/**
	 * 供应商uu
	 */
	private Long venduu;

	/**
	 * 客户uu
	 */
	private Long custuu;

	/**
	 * 是否启用b2b对账<br>
	 * 1. 启用 <br>
	 * 2. 未启用
	 * 
	 */
	private Short apcheck;

	public Long getVenduu() {
		return venduu;
	}

	public void setVenduu(Long venduu) {
		this.venduu = venduu;
	}

	public Long getCustuu() {
		return custuu;
	}

	public void setCustuu(Long custuu) {
		this.custuu = custuu;
	}

	public Short getApcheck() {
		return apcheck;
	}

	public void setApcheck(Short apcheck) {
		this.apcheck = apcheck;
	}

}