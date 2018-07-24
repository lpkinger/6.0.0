package com.uas.b2c.model;

import java.util.Date;

public class JsonPament {
	
	private String accountname;	
	private String bankname;
	private Date createTime;
	private String currency;
	private String dissociative;
	private String enuu;
	private String number;
	private String opraterUserType;
	private String useruu;
	
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getOpraterUserType() {
		return opraterUserType;
	}
	public void setOpraterUserType(String opraterUserType) {
		this.opraterUserType = opraterUserType;
	}
	public String getDissociative() {
		return dissociative;
	}
	public void setDissociative(String dissociative) {
		this.dissociative = dissociative;
	}
	public String getEnuu() {
		return enuu;
	}
	public void setEnuu(String enuu) {
		this.enuu = enuu;
	}
	public String getUseruu() {
		return useruu;
	}
	public void setUseruu(String useruu) {
		this.useruu = useruu;
	}
}
