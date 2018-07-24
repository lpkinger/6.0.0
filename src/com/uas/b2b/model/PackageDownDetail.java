package com.uas.b2b.model;

/**
 * PackageDownDetail模型
 * 
 * @author aof
 * @date 2015年11月2日
 */
public class PackageDownDetail {
	private Long pd_id;// 主表ID
	private String pd_outboxcode;// 外箱编号
	private String pd_innerboxcode;// 内向编号
	private String pd_barcode;// 序列号
	private Double pd_innerqty;// 内箱总数

	public Long getPd_id() {
		return pd_id;
	}

	public void setPd_id(Long pd_id) {
		this.pd_id = pd_id;
	}

	public String getPd_outboxcode() {
		return pd_outboxcode;
	}

	public void setPd_outboxcode(String pd_outboxcode) {
		this.pd_outboxcode = pd_outboxcode;
	}

	public String getPd_innerboxcode() {
		return pd_innerboxcode;
	}

	public void setPd_innerboxcode(String pd_innerboxcode) {
		this.pd_innerboxcode = pd_innerboxcode;
	}

	public String getPd_barcode() {
		return pd_barcode;
	}

	public void setPd_barcode(String pd_barcode) {
		this.pd_barcode = pd_barcode;
	}

	public Double getPd_innerqty() {
		return pd_innerqty;
	}

	public void setPd_innerqty(Double pd_innerqty) {
		this.pd_innerqty = pd_innerqty;
	}

	public String toSqlString(int foreginKey) {
		return "insert into packagedetail (pd_id, pd_paid, pd_outboxcode, pd_innerboxcode, pd_barcode, pd_innerqty) "
				+ " values (" + "packagedetail_seq.nextval" + ", " + foreginKey + ", '" + this.pd_outboxcode + "', '"
				+ this.pd_innerboxcode + "', '" + this.pd_barcode + "', " + this.pd_innerqty + ")";
	}
}
