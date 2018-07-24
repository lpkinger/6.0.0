package com.uas.erp.model;

/**
 * 平台企业信息查询接口
 * 
 * @author yingp
 * 
 */
public class QueriableMember {

	private String name;
	private String shortName;
	private Long uu;
	private String address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Long getUu() {
		return uu;
	}

	public void setUu(Long uu) {
		this.uu = uu;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public QueriableMember() {

	}

}
