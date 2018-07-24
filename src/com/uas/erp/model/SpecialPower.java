package com.uas.erp.model;

import java.io.Serializable;

public class SpecialPower implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sp_id;
	private int sp_sspid;
	private Integer sp_emid;
	private Integer sp_joid;

	public int getSp_id() {
		return sp_id;
	}

	public void setSp_id(int sp_id) {
		this.sp_id = sp_id;
	}

	public int getSp_sspid() {
		return sp_sspid;
	}

	public void setSp_sspid(int sp_sspid) {
		this.sp_sspid = sp_sspid;
	}

	public Integer getSp_emid() {
		return sp_emid;
	}

	public void setSp_emid(Integer sp_emid) {
		this.sp_emid = sp_emid;
	}

	public Integer getSp_joid() {
		return sp_joid;
	}

	public void setSp_joid(Integer sp_joid) {
		this.sp_joid = sp_joid;
	}

}