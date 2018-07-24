package com.uas.mobile.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.model.Master;

/**
 * Andriod设备对应的员工表
 * 
 * @author suntg
 * @date 2014年9月3日 10:47:06
 */
public class Employee implements Serializable {
	
	private static final long serialVersionUID = 9154546832037377352L;//序列编码
	private Integer em_id;// Id
	private String em_code;// 员工编号
	private Long em_uu;// 员工uu
	private String em_name;// 姓名
	private String em_sex;// 性别
	private String em_mobile;// 移动电话
	private String em_remark;// 备注
	private String em_masterIp;// 账套Ip
	private String em_master;// 当前账套名称
	private String em_masters;// 可登录的账套
	private Integer em_remind = 1;// 是否刷新寻呼,1-是,0-否
	private String em_type = "normal";// 员工账号类型,超级账号、普通账号
	private String em_password;// 登录密码
	private Integer em_enid;// 员工所在企业id
	private String em_enname;//员工所在企业名称
	private String em_position; // 岗位
	private String em_depart; // 部门
	private String em_class;// 员工类型 (试用，正式，离职)
	private String em_lastip;//最后登录IP
	private Master currentMaster;// 当前登录账套
	private Date em_mologintime;//移动客户端的最新登录时间
	
	public Date getEm_mologintime() {
		return em_mologintime;
	}
	public void setEm_mologintime(Date em_mologintime) {
		this.em_mologintime = em_mologintime;
	}
	public Master getCurrentMaster() {
		return currentMaster;
	}
	public void setCurrentMaster(Master currentMaster) {
		this.currentMaster = currentMaster;
	}
	public Integer getEm_id() {
		return em_id;
	}
	public void setEm_id(Integer em_id) {
		this.em_id = em_id;
	}
	public String getEm_code() {
		return em_code;
	}
	public void setEm_code(String em_code) {
		this.em_code = em_code;
	}
	public Long getEm_uu() {
		return em_uu;
	}
	public void setEm_uu(Long em_uu) {
		this.em_uu = em_uu;
	}
	public String getEm_name() {
		return em_name;
	}
	public void setEm_name(String em_name) {
		this.em_name = em_name;
	}
	public String getEm_sex() {
		return em_sex;
	}
	public void setEm_sex(String em_sex) {
		this.em_sex = em_sex;
	}
	public String getEm_mobile() {
		return em_mobile;
	}
	public void setEm_mobile(String em_mobile) {
		this.em_mobile = em_mobile;
	}
	public String getEm_remark() {
		return em_remark;
	}
	public void setEm_remark(String em_remark) {
		this.em_remark = em_remark;
	}
	public String getEm_masterIp() {
		return em_masterIp;
	}
	public void setEm_masterIp(String em_masterIp) {
		this.em_masterIp = em_masterIp;
	}
	public String getEm_master() {
		return em_master;
	}
	public void setEm_master(String em_master) {
		this.em_master = em_master;
	}
	public String getEm_masters() {
		return em_masters;
	}
	public void setEm_masters(String em_masters) {
		this.em_masters = em_masters;
	}
	public Integer getEm_remind() {
		return em_remind;
	}
	public void setEm_remind(Integer em_remind) {
		this.em_remind = em_remind;
	}
	public String getEm_type() {
		return em_type;
	}
	public void setEm_type(String em_type) {
		this.em_type = em_type;
	}
	public String getEm_password() {
		return em_password;
	}
	public void setEm_password(String em_password) {
		this.em_password = em_password;
	}
	public Integer getEm_enid() {
		return em_enid;
	}
	public void setEm_enid(Integer em_enid) {
		this.em_enid = em_enid;
	}
	public String getEm_enname() {
		return em_enname;
	}
	public void setEm_enname(String em_enname) {
		this.em_enname = em_enname;
	}
	public String getEm_position() {
		return em_position;
	}
	public void setEm_position(String em_position) {
		this.em_position = em_position;
	}
	public String getEm_depart() {
		return em_depart;
	}
	public void setEm_depart(String em_depart) {
		this.em_depart = em_depart;
	}
	public String getEm_class() {
		return em_class;
	}
	public void setEm_class(String em_class) {
		this.em_class = em_class;
	}
	public String getEm_lastip() {
		return em_lastip;
	}
	public void setEm_lastip(String em_lastip) {
		this.em_lastip = em_lastip;
	}

}
