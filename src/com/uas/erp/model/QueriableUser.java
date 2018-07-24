package com.uas.erp.model;

/**
 * 平台的个人信息查询接口
 * 
 * @author yingp
 * 
 */
public class QueriableUser {

	private String name;
	private String tel;
	private String email;
	private Long uu;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getUu() {
		return uu;
	}

	public void setUu(Long uu) {
		this.uu = uu;
	}

	public QueriableUser() {
	}

}
