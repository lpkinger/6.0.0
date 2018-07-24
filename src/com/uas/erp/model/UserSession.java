package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线用户
 * 
 * @author yingp
 */
public class UserSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int em_id;
	private String em_code;
	private String em_name;
	private Integer em_orgid;
	private String sid;
	private String ip;
	private Date date;
	private String sob;
	private Integer em_pdamobilelogin;
	/**
	 * 锁定状态<br>
	 * 账号被管理员强制下线
	 */
	private boolean locked = false;
	
	/**
	 * 账号被扫码登录的方式踢下线
	 */
	private boolean kicked = false;
	

	public int getEm_id() {
		return em_id;
	}

	public void setEm_id(int em_id) {
		this.em_id = em_id;
	}

	public String getEm_code() {
		return em_code;
	}

	public void setEm_code(String em_code) {
		this.em_code = em_code;
	}

	public Integer getEm_orgid() {
		return em_orgid;
	}

	public void setEm_orgid(Integer em_orgid) {
		this.em_orgid = em_orgid;
	}

	public String getEm_name() {
		return em_name;
	}

	public void setEm_name(String em_name) {
		this.em_name = em_name;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSob() {
		return sob;
	}

	public void setSob(String sob) {
		this.sob = sob;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isKicked() {
		return kicked;
	}

	public void setKicked(boolean kicked) {
		this.kicked = kicked;
	}

	public UserSession() {

	}

	/**
	 * @param sob
	 *            帐套名称
	 * @param em_id
	 *            账号ID
	 * @param em_code
	 *            账号
	 * @param em_name
	 *            账号名称
	 * @param em_orgid
	 *            组织ID
	 * @param sid
	 *            sessionID
	 * @param ip
	 *            登录IP
	 * @param date
	 *            登录时间
	 */
	public UserSession(String sob, int em_id, String em_code, String em_name, Integer em_orgid, String sid, String ip, Date date,
			Integer em_pdamobilelogin) {
		this();
		this.sob = sob;
		this.em_id = em_id;
		this.em_code = em_code;
		this.em_name = em_name;
		this.em_orgid = em_orgid;
		this.sid = sid;
		this.ip = ip;
		this.date = date;
		this.em_pdamobilelogin = em_pdamobilelogin;
	}

	@Override
	public int hashCode() {
		return this.sid.hashCode();
	}

	@Override
	public boolean equals(Object paramObject) {
		if (paramObject == null)
			return false;
		if (this == paramObject)
			return true;
		if (paramObject instanceof UserSession) {
			UserSession us = (UserSession) paramObject;
			if (us.getSid().equals(this.sid))
				return true;
		}
		return false;
	}

	public Integer getEm_pdamobilelogin() {
		return em_pdamobilelogin;
	}

	public void setEm_pdamobilelogin(Integer em_pdamobilelogin) {
		this.em_pdamobilelogin = em_pdamobilelogin;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
