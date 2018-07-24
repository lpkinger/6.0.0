package com.uas.erp.model;

/**
 * 凭证辅助核算
 * 
 * @date 2017年2月9日下午6:26:59
 * @author yingp
 *
 */
public class VoucherDetailAss {

	private int vds_id;
	private int vds_vdid;
	private String vds_asstype;
	private String vds_asscode;
	private String vds_assname;

	public int getVds_id() {
		return vds_id;
	}

	public void setVds_id(int vds_id) {
		this.vds_id = vds_id;
	}

	public int getVds_vdid() {
		return vds_vdid;
	}

	public void setVds_vdid(int vds_vdid) {
		this.vds_vdid = vds_vdid;
	}

	public String getVds_asstype() {
		return vds_asstype;
	}

	public void setVds_asstype(String vds_asstype) {
		this.vds_asstype = vds_asstype;
	}

	public String getVds_asscode() {
		return vds_asscode;
	}

	public void setVds_asscode(String vds_asscode) {
		this.vds_asscode = vds_asscode;
	}

	public String getVds_assname() {
		return vds_assname;
	}

	public void setVds_assname(String vds_assname) {
		this.vds_assname = vds_assname;
	}

}
