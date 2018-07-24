package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class KnowledgeModule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int km_id;
	private String km_name;
	private String km_recorder;
	private int km_recorderid;
	private Date km_recorddate;
	private String km_remark;
	private int km_number;
	private int km_corchild;
	private int km_personid;

	public int getKm_id() {
		return km_id;
	}

	public void setKm_id(int km_id) {
		this.km_id = km_id;
	}

	public String getKm_name() {
		return km_name;
	}

	public void setKm_name(String km_name) {
		this.km_name = km_name;
	}

	public String getKm_recorder() {
		return km_recorder;
	}

	public void setKm_recorder(String km_recorder) {
		this.km_recorder = km_recorder;
	}

	public int getKm_recorderid() {
		return km_recorderid;
	}

	public void setKm_recorderid(int km_recorderid) {
		this.km_recorderid = km_recorderid;
	}

	public Date getKm_recorddate() {
		return km_recorddate;
	}

	public void setKm_recorddate(Date km_recorddate) {
		this.km_recorddate = km_recorddate;
	}

	public String getKm_remark() {
		return km_remark;
	}

	public void setKm_remark(String km_remark) {
		this.km_remark = km_remark;
	}

	public int getKm_number() {
		return km_number;
	}

	public void setKm_number(int km_number) {
		this.km_number = km_number;
	}

	public int getKm_corchild() {
		return km_corchild;
	}

	public void setKm_corchild(int km_corchild) {
		this.km_corchild = km_corchild;
	}

	public int getKm_personid() {
		return km_personid;
	}

	public void setKm_personid(int km_personid) {
		this.km_personid = km_personid;
	}

}
