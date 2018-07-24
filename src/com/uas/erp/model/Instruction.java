package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class Instruction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int in_id;
	private String in_type;
	private String in_secretlevel;
	private String in_emergencydegree;
	private String in_title;
	private Date in_date;
	private String in_number;
	private String in_model;
	private String in_man;
	private int in_man_id;
	private String in_dept;
	private String in_attach;
	private String in_context;
	private String in_leader;
	private String in_leader_id;
	private String in_status;
	private String in_statuscode;
	private int in_money;

	public int getIn_id() {
		return in_id;
	}

	public void setIn_id(int in_id) {
		this.in_id = in_id;
	}

	public String getIn_type() {
		return in_type;
	}

	public void setIn_type(String in_type) {
		this.in_type = in_type;
	}

	public String getIn_secretlevel() {
		return in_secretlevel;
	}

	public void setIn_secretlevel(String in_secretlevel) {
		this.in_secretlevel = in_secretlevel;
	}

	public String getIn_emergencydegree() {
		return in_emergencydegree;
	}

	public void setIn_emergencydegree(String in_emergencydegree) {
		this.in_emergencydegree = in_emergencydegree;
	}

	public String getIn_title() {
		return in_title;
	}

	public void setIn_title(String in_title) {
		this.in_title = in_title;
	}

	public Date getIn_date() {
		return in_date;
	}

	public void setIn_date(Date in_date) {
		this.in_date = in_date;
	}

	public String getIn_number() {
		return in_number;
	}

	public void setIn_number(String in_number) {
		this.in_number = in_number;
	}

	public String getIn_model() {
		return in_model;
	}

	public void setIn_model(String in_model) {
		this.in_model = in_model;
	}

	public String getIn_man() {
		return in_man;
	}

	public void setIn_man(String in_man) {
		this.in_man = in_man;
	}

	public int getIn_man_id() {
		return in_man_id;
	}

	public void setIn_man_id(int in_man_id) {
		this.in_man_id = in_man_id;
	}

	public String getIn_dept() {
		return in_dept;
	}

	public void setIn_dept(String in_dept) {
		this.in_dept = in_dept;
	}

	public String getIn_attach() {
		return in_attach;
	}

	public void setIn_attach(String in_attach) {
		this.in_attach = in_attach;
	}

	public String getIn_context() {
		return in_context;
	}

	public void setIn_context(String in_context) {
		this.in_context = in_context;
	}

	public String getIn_leader() {
		return in_leader;
	}

	public void setIn_leader(String in_leader) {
		this.in_leader = in_leader;
	}

	public String getIn_leader_id() {
		return in_leader_id;
	}

	public void setIn_leader_id(String in_leader_id) {
		this.in_leader_id = in_leader_id;
	}

	public String getIn_status() {
		return in_status;
	}

	public void setIn_status(String in_status) {
		this.in_status = in_status;
	}

	public String getIn_statuscode() {
		return in_statuscode;
	}

	public void setIn_statuscode(String in_statuscode) {
		this.in_statuscode = in_statuscode;
	}

	public int getIn_money() {
		return in_money;
	}

	public void setIn_money(int in_money) {
		this.in_money = in_money;
	}

}
