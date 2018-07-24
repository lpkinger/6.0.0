package com.uas.b2b.model;

import java.util.Date;

/**
 * 代采订单传输到ERP的日志记录（错误记录统计）
 * 
 * @author hejq
 * @time 创建时间：2017年6月5日
 */
public class DeputyOrdersLog {

	/**
	 * 序号
	 */
	private Long id;

	/**
	 * 企业uu
	 */
	private Long enuu;
	
	/**
	 * 原订单编号id
	 */
	private Long sourceid;

	/**
	 * 订单编号
	 */
	private String code;
	
	/**
	 * 下载的数据大小
	 */
	private Integer downloadSize;

	/**
	 * 消息
	 */
	private String message;

	/**
	 * 时间
	 */
	private Date date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public Long getSourceid() {
		return sourceid;
	}

	public void setSourceid(Long sourceid) {
		this.sourceid = sourceid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getDownloadSize() {
		return downloadSize;
	}

	public void setDownloadSize(Integer downloadSize) {
		this.downloadSize = downloadSize;
	}


}
