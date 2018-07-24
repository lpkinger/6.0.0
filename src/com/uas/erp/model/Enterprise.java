package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 企业表
 * 
 * @author yingp
 * @date 2012-7-26 0:05:35
 */
public class Enterprise implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int en_Id;
	private String en_Name; // 公司名称
	private String en_Shortname; // 简称
	private String en_Type; // 公司类型
	private String en_Status;// 状态
	private String en_Name_En; // 英文名称
	private String en_Address; // 注册地址
	private String en_Deliveraddr; // 默认送货地址
	private String en_Tel; // 电话
	private String en_Fax; // 传真
	private String en_Corporation; // 公司法人
	private String en_Businesscode; // 商业登记证号
	private String en_Taxcode; // 纳税人识别人
	private String en_Registercapital; // 注册资本
	private String en_Url; // 公司网址
	private Integer en_uu; // 企业ID号
	private String en_Time; // 注册时间
	private String en_EmailCode;// 邮箱验证码
	private String en_Admin;// 管理员名
	private String en_Adminphone;// 管理员电话
	private String en_Email;// 管理员邮箱
	private String en_Attachment;// 附件路径
	private String en_password;// 企业
	private String en_printurl;// 打印地址
	private String en_whichsystem;// 帐套
	private String en_msguc;
	private String en_msgpwd;
	private Long en_adminuu;// 管理员UU号
	private String en_adminuupassword;// 管理员UU密码
	private String en_adminemail;// 管理员邮箱
    private String en_erpurl;//ERP外网地址
    private String en_intrajasperurl;//jasper打印内网地址
    private String en_extrajasperurl;//jasper打印外网地址
	public String getEn_intrajasperurl() {
		return en_intrajasperurl;
	}

	public void setEn_intrajasperurl(String en_intrajasperurl) {
		this.en_intrajasperurl = en_intrajasperurl;
	}

	public String getEn_extrajasperurl() {
		return en_extrajasperurl;
	}

	public void setEn_extrajasperurl(String en_extrajasperurl) {
		this.en_extrajasperurl = en_extrajasperurl;
	}

	public String getEn_Admin() {
		return en_Admin;
	}

	public void setEn_Admin(String en_Admin) {
		this.en_Admin = en_Admin;
	}

	public String getEn_Adminphone() {
		return en_Adminphone;
	}

	public void setEn_Adminphone(String en_Adminphone) {
		this.en_Adminphone = en_Adminphone;
	}

	public int getEn_Id() {
		return en_Id;
	}

	public void setEn_Id(int en_Id) {
		this.en_Id = en_Id;
	}

	public String getEn_Email() {
		return en_Email;
	}

	public void setEn_Email(String en_Email) {
		this.en_Email = en_Email;
	}

	public String getEn_EmailCode() {
		return en_EmailCode;
	}

	public void setEn_EmailCode(String en_EmailCode) {
		this.en_EmailCode = en_EmailCode;
	}

	public String getEn_Name() {
		return en_Name;
	}

	public void setEn_Name(String en_Name) {
		this.en_Name = en_Name;
	}

	public String getEn_Shortname() {
		return en_Shortname;
	}

	public void setEn_Shortname(String en_Shortname) {
		this.en_Shortname = en_Shortname;
	}

	public String getEn_Type() {
		return en_Type;
	}

	public void setEn_Type(String en_Type) {
		this.en_Type = en_Type;
	}

	public String getEn_printurl() {
		return en_printurl;
	}

	public void setEn_printurl(String en_printurl) {
		this.en_printurl = en_printurl;
	}

	public String getEn_password() {
		return en_password;
	}

	public void setEn_password(String en_password) {
		this.en_password = en_password;
	}

	public String getEn_Status() {
		return en_Status;
	}

	public void setEn_Status(String en_Status) {
		this.en_Status = en_Status;
	}

	public String getEn_Name_En() {
		return en_Name_En;
	}

	public void setEn_Name_En(String en_Name_En) {
		this.en_Name_En = en_Name_En;
	}

	public String getEn_Address() {
		return en_Address;
	}

	public void setEn_Address(String en_Address) {
		this.en_Address = en_Address;
	}

	public String getEn_Deliveraddr() {
		return en_Deliveraddr;
	}

	public void setEn_Deliveraddr(String en_Deliveraddr) {
		this.en_Deliveraddr = en_Deliveraddr;
	}

	public String getEn_Tel() {
		return en_Tel;
	}

	public void setEn_Tel(String en_Tel) {
		this.en_Tel = en_Tel;
	}

	public String getEn_Fax() {
		return en_Fax;
	}

	public void setEn_Fax(String en_Fax) {
		this.en_Fax = en_Fax;
	}

	public String getEn_Corporation() {
		return en_Corporation;
	}

	public void setEn_Corporation(String en_Corporation) {
		this.en_Corporation = en_Corporation;
	}

	public String getEn_Businesscode() {
		return en_Businesscode;
	}

	public void setEn_Businesscode(String en_Businesscode) {
		this.en_Businesscode = en_Businesscode;
	}

	public String getEn_Taxcode() {
		return en_Taxcode;
	}

	public void setEn_Taxcode(String en_Taxcode) {
		this.en_Taxcode = en_Taxcode;
	}

	public String getEn_Registercapital() {
		return en_Registercapital;
	}

	public void setEn_Registercapital(String en_Registercapital) {
		this.en_Registercapital = en_Registercapital;
	}

	public String getEn_Url() {
		return en_Url;
	}

	public void setEn_Url(String en_Url) {
		this.en_Url = en_Url;
	}

	public Integer getEn_uu() {
		return en_uu;
	}

	public void setEn_uu(Integer en_uu) {
		this.en_uu = en_uu;
	}

	public String getEn_Time() {
		return en_Time;
	}

	public void setEn_Time(String en_Time) {
		this.en_Time = en_Time;
	}

	public String getEn_Attachment() {
		return en_Attachment;
	}

	public void setEn_Attachment(String en_Attachment) {
		this.en_Attachment = en_Attachment;
	}

	public String getEn_msguc() {
		return en_msguc;
	}

	public void setEn_msguc(String en_msguc) {
		this.en_msguc = en_msguc;
	}

	public String getEn_msgpwd() {
		return en_msgpwd;
	}

	public void setEn_msgpwd(String en_msgpwd) {
		this.en_msgpwd = en_msgpwd;
	}

	@Override
	public String toString() {
		return "公司名称：" + en_Name;
	}

	@Override
	public String table() {
		return "Enterprise";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "en_Id" };
	}

	public void setEn_whichsystem(String en_whichsystem) {
		this.en_whichsystem = en_whichsystem;
	}

	public String getEn_whichsystem() {
		return en_whichsystem;
	}

	public Long getEn_adminuu() {
		return en_adminuu;
	}

	public void setEn_adminuu(Long en_adminuu) {
		this.en_adminuu = en_adminuu;
	}

	public String getEn_adminuupassword() {
		return en_adminuupassword;
	}

	public void setEn_adminuupassword(String en_adminuupassword) {
		this.en_adminuupassword = en_adminuupassword;
	}
    
	public String getEn_erpurl() {
		return en_erpurl;
	}

	public void setEn_erpurl(String en_erpurl) {
		this.en_erpurl = en_erpurl;
	}

	/**
	 * 获取访问B2B平台的管理员身份验证参数
	 * 
	 * @return
	 */
	public String b2bKey() {
		return "b_username=" + this.en_adminuu + "&b_password=" + this.en_adminuupassword + "&b_enuu=" + this.en_uu;
	}

	public String getEn_adminemail() {
		return en_adminemail;
	}

	public void setEn_adminemail(String en_adminemail) {
		this.en_adminemail = en_adminemail;
	}

}