package com.uas.erp.model;

import java.io.Serializable;

public class AccountInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 账套ID（管理平台）
	 */
	private Long masterId;
	/**
	 * 企业UU号
	 */
	private Long enUU;
	/**
	 * 个人UU号
	 */
	private Long userUU;
	private String name;
	/**
	 * 账号
	 */
	private String code;
	/**
	 * 手机号
	 */
	private String tel;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 出生日期
	 */
	private String birthday;
	/**
	 * 性别
	 */
	private int sex;
	/**
	 * 密码（明文）
	 */
	private String password;
	/*
	 * 账套ID
	 */
	private String accountId;
	
	/**
	 * 密码（明文）
	 */
    private String imid;
	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	public void setEnUU(Long enUU) {
		this.enUU = enUU;
	}

	public Long getEnUU() {
		return enUU;
	}

	public Long getUserUU() {
		return userUU;
	}

	public void setUserUU(Long userUU) {
		this.userUU = userUU;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getImid() {
		return imid;
	}

	public void setImid(String imid) {
		this.imid = imid;
	}

}
