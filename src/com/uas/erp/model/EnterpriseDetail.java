package com.uas.erp.model;

public class EnterpriseDetail {
	private String name; //企业名称
	private String shortName; //企业简称
	private Long uu; //UU号
	private String address; //企业地址
	private String url;  //企业主页
	private String management; //经营模式
	private String products;  //主营产品
	private String infos;    //简介
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getManagement() {
		return management;
	}
	public void setManagement(String management) {
		this.management = management;
	}
	public String getProducts() {
		return products;
	}
	public void setProducts(String products) {
		this.products = products;
	}
	public String getInfos() {
		return infos;
	}
	public void setInfos(String infos) {
		this.infos = infos;
	}
	
	public EnterpriseDetail() {

	}	

}
