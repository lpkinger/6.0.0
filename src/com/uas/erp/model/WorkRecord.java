package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class WorkRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int wr_id;
	private String wr_recorder;
	private int wr_recorderemid;
	private Date wr_recorddate;

	public int getWr_id() {
		return wr_id;
	}

	public void setWr_id(int wr_id) {
		this.wr_id = wr_id;
	}

	public String getWr_recorder() {
		return wr_recorder;
	}

	public void setWr_recorder(String wr_recorder) {
		this.wr_recorder = wr_recorder;
	}

	public int getWr_recorderemid() {
		return wr_recorderemid;
	}

	public void setWr_recorderemid(int wr_recorderemid) {
		this.wr_recorderemid = wr_recorderemid;
	}

	public Date getWr_recorddate() {
		return wr_recorddate;
	}

	public void setWr_recorddate(Date wr_recorddate) {
		this.wr_recorddate = wr_recorddate;
	}

}
