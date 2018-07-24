package com.uas.erp.model;

public class RoleSpecialPower {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int rp_id;
	private int sp_sspid;
	private Integer sp_roid;

	public int getRp_id() {
		return rp_id;
	}

	public void setRp_id(int rp_id) {
		this.rp_id = rp_id;
	}

	public Integer getSp_roid() {
		return sp_roid;
	}

	public void setSp_roid(Integer sp_roid) {
		this.sp_roid = sp_roid;
	}

	public int getSp_sspid() {
		return sp_sspid;
	}

	public void setSp_sspid(int sp_sspid) {
		this.sp_sspid = sp_sspid;
	}

}
