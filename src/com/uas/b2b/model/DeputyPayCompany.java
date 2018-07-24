package com.uas.b2b.model;

public class DeputyPayCompany {

	/**
	 * 付款企业名称（因付款企业可能不属于平台，未设置uu）
	 */
	private String enname;

	/**
	 * 付款企业地址
	 */
	private String address;

	/**
	 * 联系人
	 */
	private String user;

	/**
	 * 联系电话
	 */
	private String usertel;

	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUsertel() {
		return usertel;
	}

	public void setUsertel(String usertel) {
		this.usertel = usertel;
	}

}
