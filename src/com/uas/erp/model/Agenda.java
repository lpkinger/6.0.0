package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class Agenda implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ag_id;
	private String ag_type;
	private int ag_atid;
	private String ag_title;
	private String ag_content;
	private Date ag_start;
	private Date ag_end;
	private int ag_isweek;
	private int ag_ismessage;
	private int ag_arrange_id;
	private String ag_arrange;
	private String ag_executor;
	private String ag_executor_id;
	private Date ag_predict;
	private int ag_issecrecy;

	public int getAg_id() {
		return ag_id;
	}

	public void setAg_id(int ag_id) {
		this.ag_id = ag_id;
	}

	public String getAg_type() {
		return ag_type;
	}

	public void setAg_type(String ag_type) {
		this.ag_type = ag_type;
	}

	public int getAg_atid() {
		return ag_atid;
	}

	public void setAg_atid(int ag_atid) {
		this.ag_atid = ag_atid;
	}

	public String getAg_title() {
		return ag_title;
	}

	public void setAg_title(String ag_title) {
		this.ag_title = ag_title;
	}

	public String getAg_content() {
		return ag_content;
	}

	public void setAg_content(String ag_content) {
		this.ag_content = ag_content;
	}

	public Date getAg_start() {
		return ag_start;
	}

	public void setAg_start(Date ag_start) {
		this.ag_start = ag_start;
	}

	public Date getAg_end() {
		return ag_end;
	}

	public void setAg_end(Date ag_end) {
		this.ag_end = ag_end;
	}

	public int getAg_isweek() {
		return ag_isweek;
	}

	public void setAg_isweek(int ag_isweek) {
		this.ag_isweek = ag_isweek;
	}

	public int getAg_ismessage() {
		return ag_ismessage;
	}

	public void setAg_ismessage(int ag_ismessage) {
		this.ag_ismessage = ag_ismessage;
	}

	public int getAg_arrange_id() {
		return ag_arrange_id;
	}

	public void setAg_arrange_id(int ag_arrange_id) {
		this.ag_arrange_id = ag_arrange_id;
	}

	public String getAg_arrange() {
		return ag_arrange;
	}

	public void setAg_arrange(String ag_arrange) {
		this.ag_arrange = ag_arrange;
	}

	public String getAg_executor() {
		return ag_executor;
	}

	public void setAg_executor(String ag_executor) {
		this.ag_executor = ag_executor;
	}

	public String getAg_executor_id() {
		return ag_executor_id;
	}

	public void setAg_executor_id(String ag_executor_id) {
		this.ag_executor_id = ag_executor_id;
	}

	public Date getAg_predict() {
		return ag_predict;
	}

	public void setAg_predict(Date ag_predict) {
		this.ag_predict = ag_predict;
	}

	public int getAg_issecrecy() {
		return ag_issecrecy;
	}

	public void setAg_issecrecy(int ag_issecrecy) {
		this.ag_issecrecy = ag_issecrecy;
	}

}
