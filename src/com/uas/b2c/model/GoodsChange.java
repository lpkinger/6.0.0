package com.uas.b2c.model;

import java.io.Serializable;

/**
 * 修改商品数
 * 
 * @author yingp
 *
 */
public class GoodsChange implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 唯一标识
	 */
	private Long sourceId;

	/**
	 * 平台批次
	 */
	private String batchCode;

	/**
	 * 新数量
	 */
	private Double reverse;

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Double getReverse() {
		return reverse;
	}

	public void setReverse(Double reverse) {
		this.reverse = reverse;
	}

}
