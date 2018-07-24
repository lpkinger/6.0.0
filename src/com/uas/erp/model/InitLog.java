package com.uas.erp.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.uas.erp.dao.Saveable;

/**
 * 导入数据主表
 */
public class InitLog implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int il_id;
	private String il_caller;// Caller
	private int il_sequence;// 次数
	private Timestamp il_date;// 日期
	private int il_checked;// 是否校验//只有InitData对应数据全部校验完成时，才为1
	private int il_success;// 是否通过//
	private int il_toformal;// 是否转正式//
	private int il_count;// 数据size
	private String il_result;
	private String il_man;   //操作人

	public String getIl_man() {
		return il_man;
	}

	public void setIl_man(String il_man) {
		this.il_man = il_man;
	}

	public int getIl_id() {
		return il_id;
	}

	public void setIl_id(int il_id) {
		this.il_id = il_id;
	}

	public String getIl_caller() {
		return il_caller;
	}

	public void setIl_caller(String il_caller) {
		this.il_caller = il_caller;
	}

	public int getIl_sequence() {
		return il_sequence;
	}

	public void setIl_sequence(int il_sequence) {
		this.il_sequence = il_sequence;
	}

	public Timestamp getIl_date() {
		return il_date;
	}

	public void setIl_date(Timestamp il_date) {
		this.il_date = il_date;
	}

	public int getIl_checked() {
		return il_checked;
	}

	public void setIl_checked(int il_checked) {
		this.il_checked = il_checked;
	}

	public int getIl_count() {
		return il_count;
	}

	public void setIl_count(int il_count) {
		this.il_count = il_count;
	}

	public int getIl_success() {
		return il_success;
	}

	public void setIl_success(int il_success) {
		this.il_success = il_success;
	}

	public String getIl_result() {
		return il_result;
	}

	public void setIl_result(String il_result) {
		this.il_result = il_result;
	}

	public int getIl_toformal() {
		return il_toformal;
	}

	public void setIl_toformal(int il_toformal) {
		this.il_toformal = il_toformal;
	}

	@Override
	public String table() {
		return "InitLog";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "il_id" };
	}

}
