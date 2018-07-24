package com.uas.b2c.model;

import java.io.Serializable;
import java.util.Date;

/**
 * token信息
 * 
 * @author yingp
 *
 */
public class AccessToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String access_token;

	private Date time;

	private int expires_in;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	/**
	 * 是否过期
	 * 
	 * @return
	 */
	public boolean isExpired() {
		return System.currentTimeMillis() - this.time.getTime() > expires_in * 1000;
	}

}
