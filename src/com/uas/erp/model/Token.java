package com.uas.erp.model;

import java.util.Date;

/**
 * 二维码token对象
 * @author Administrator
 *
 */
public class Token {
	
	private Date time;
	//状态码
	private String status;
	//员工编号
	private String em_code;
	//套账
	private String sob;
	
	
	public Token() {
	}

	public Token(Date time) {
		super();
		this.time = time;
		this.status = "";
		this.em_code = "";
		this.sob = "";
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEm_code() {
		return em_code;
	}

	public void setEm_code(String em_code) {
		this.em_code = em_code;
	}

	public String getSob() {
		return sob;
	}

	public void setSob(String sob) {
		this.sob = sob;
	}

	@Override
	public String toString() {
		return "Token [time=" + time + ", status=" + status + ", em_code=" + em_code + ", sob=" + sob + "]";
	}
	
	
	
	
	
	
}
