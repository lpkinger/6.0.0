package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 员工好友邮箱
 * 
 * @author yingp
 */
public class EmployeeMail implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int emm_id;
	private int emm_emid;
	private int emm_parentid;
	private String emm_leaf;
	private String emm_friendname;
	private String emm_friendmail;
	private String emm_friendgroup;
	private String emm_depart;
	private String emm_post;
	private String emm_phone;
	private String emm_addr;

	public String getEmm_depart() {
		return emm_depart;
	}

	public void setEmm_depart(String emm_depart) {
		this.emm_depart = emm_depart;
	}

	public String getEmm_post() {
		return emm_post;
	}

	public void setEmm_post(String emm_post) {
		this.emm_post = emm_post;
	}

	public String getEmm_phone() {
		return emm_phone;
	}

	public void setEmm_phone(String emm_phone) {
		this.emm_phone = emm_phone;
	}

	public String getEmm_addr() {
		return emm_addr;
	}

	public void setEmm_addr(String emm_addr) {
		this.emm_addr = emm_addr;
	}

	public int getEmm_id() {
		return emm_id;
	}

	public void setEmm_id(int emm_id) {
		this.emm_id = emm_id;
	}

	public int getEmm_emid() {
		return emm_emid;
	}

	public void setEmm_emid(int emm_emid) {
		this.emm_emid = emm_emid;
	}

	public String getEmm_friendname() {
		return emm_friendname;
	}

	public int getEmm_parentid() {
		return emm_parentid;
	}

	public void setEmm_parentid(int emm_parentid) {
		this.emm_parentid = emm_parentid;
	}

	public void setEmm_friendname(String emm_friendname) {
		this.emm_friendname = emm_friendname;
	}

	public String getEmm_friendmail() {
		return emm_friendmail;
	}

	public void setEmm_friendmail(String emm_friendmail) {
		this.emm_friendmail = emm_friendmail;
	}

	public String getEmm_friendgroup() {
		return emm_friendgroup;
	}

	public void setEmm_friendgroup(String emm_friendgroup) {
		this.emm_friendgroup = emm_friendgroup;
	}

	@Override
	public String table() {
		return "EmployeeMail";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "emm_id" };
	}

	public String getEmm_leaf() {
		return emm_leaf;
	}

	public void setEmm_leaf(String emm_leaf) {
		this.emm_leaf = emm_leaf;
	}

	public EmployeeMail() {

	}

	public EmployeeMail(Employee employee) {
		this.emm_emid = -employee.getEm_id();
		this.emm_friendname = employee.getEm_name();
		this.emm_friendmail = employee.getEm_email();
		this.emm_depart = employee.getEm_depart();
		this.emm_post = employee.getEm_position();
		this.emm_phone = employee.getEm_tel();
		this.emm_addr = employee.getEm_address();
		// TODO
	}
}
