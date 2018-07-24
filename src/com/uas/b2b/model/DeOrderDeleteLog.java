package com.uas.b2b.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 代采订单的删除记录（为了同步到ERP进行删除）
 * 
 * @author hejq
 * @time 创建时间：2017年8月18日
 */
public class DeOrderDeleteLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	/**
	 * 代采企业uu号
	 */
	private Long deputyuu;

	/**
	 * 订单编号
	 */
	private String code;

	/**
	 * 当前企业uu
	 */
	private Long enuu;

	/**
	 * 操作人uu
	 */
	private Long useruu;

	/**
	 * 操作日期
	 */
	private Date date;

	/**
	 * 下载状态<br>
	 * 202 待下载<br>
	 * 203 已下载
	 */
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDeputyuu() {
		return deputyuu;
	}

	public void setDeputyuu(Long deputyuu) {
		this.deputyuu = deputyuu;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public Long getUseruu() {
		return useruu;
	}

	public void setUseruu(Long useruu) {
		this.useruu = useruu;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
