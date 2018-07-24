package com.uas.erp.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.uas.erp.dao.Saveable;

/**
 *数据批量更新历史记录 
 */
public class UpdateSchemeLog implements Saveable, Serializable {
	
	private static final long serialVersionUID = 1L;
	private int ul_id;
	private int ul_usid;//关联scheme表id
	private int ul_sequence;
	private Timestamp ul_date;//更新日期
	private String ul_man;// 更新人编号
	private String ul_count;//更新条数
	private String ul_result;//字段类型	
	private Integer ul_success;//是否更新成功
	private Integer ul_checked;//是否校验成功
	

	public int getUl_id() {
		return ul_id;
	}

	public void setUl_id(int ul_id) {
		this.ul_id = ul_id;
	}

	public int getUl_usid() {
		return ul_usid;
	}

	public void setUl_usid(int ul_usid) {
		this.ul_usid = ul_usid;
	}

	public int getUl_sequence() {
		return ul_sequence;
	}

	public void setUl_sequence(int ul_sequence) {
		this.ul_sequence = ul_sequence;
	}

	public Timestamp getUl_date() {
		return ul_date;
	}

	public void setUl_date(Timestamp ul_date) {
		this.ul_date = ul_date;
	}

	public String getUl_man() {
		return ul_man;
	}

	public void setUl_man(String ul_man) {
		this.ul_man = ul_man;
	}

	public String getUl_count() {
		return ul_count;
	}

	public void setUl_count(String ul_count) {
		this.ul_count = ul_count;
	}

	public String getUl_result() {
		return ul_result;
	}

	public void setUl_result(String ul_result) {
		this.ul_result = ul_result;
	}

	public Integer getUl_success() {
		return ul_success;
	}

	public void setUl_success(Integer ul_success) {
		this.ul_success = ul_success;
	}

	public Integer getUl_checked() {
		return ul_checked;
	}

	public void setUl_checked(Integer ul_checked) {
		this.ul_checked = ul_checked;
	}

	@Override
	public String table() {
		return "UpdateSchemeLog";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ul_id" };
	}
}

