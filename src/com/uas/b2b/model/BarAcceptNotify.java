package com.uas.b2b.model;

/**
 * 保存erp数据库BarAcceptNotify
 * 
 * @author aof
 * @date 2015年10月30日
 */

public class BarAcceptNotify {
	private String bcn_barcode;
	private Long bcn_anid;
	private String bcn_ancode;
	private Long bcn_andid;
	private Double bcn_anddetno;
	private String bcn_prodcode;
	private Long bcn_prodid;
	private Double bcn_qty;
	private Long bcn_vendcode;
	private String bcn_outboxcode;
	private Long bcn_outboxid;
	private String bcn_status;

	public String getBcn_barcode() {
		return bcn_barcode;
	}

	public void setBcn_barcode(String bcn_barcode) {
		this.bcn_barcode = bcn_barcode;
	}

	public Long getBcn_anid() {
		return bcn_anid;
	}

	public void setBcn_anid(Long bcn_anid) {
		this.bcn_anid = bcn_anid;
	}

	public String getBcn_ancode() {
		return bcn_ancode;
	}

	public void setBcn_ancode(String bcn_ancode) {
		this.bcn_ancode = bcn_ancode;
	}

	public Long getBcn_andid() {
		return bcn_andid;
	}

	public void setBcn_andid(Long bcn_andid) {
		this.bcn_andid = bcn_andid;
	}

	public Double getBcn_anddetno() {
		return bcn_anddetno;
	}

	public void setBcn_anddetno(Double bcn_anddetno) {
		this.bcn_anddetno = bcn_anddetno;
	}

	public String getBcn_prodcode() {
		return bcn_prodcode;
	}

	public void setBcn_prodcode(String bcn_prodcode) {
		this.bcn_prodcode = bcn_prodcode;
	}

	public Long getBcn_prodid() {
		return bcn_prodid;
	}

	public void setBcn_prodid(Long bcn_prodid) {
		this.bcn_prodid = bcn_prodid;
	}

	public Double getBcn_qty() {
		return bcn_qty;
	}

	public void setBcn_qty(Double bcn_qty) {
		this.bcn_qty = bcn_qty;
	}

	public Long getBcn_vendcode() {
		return bcn_vendcode;
	}

	public void setBcn_vendcode(Long bcn_vendcode) {
		this.bcn_vendcode = bcn_vendcode;
	}

	public String getBcn_outboxcode() {
		return bcn_outboxcode;
	}

	public void setBcn_outboxcode(String bcn_outboxcode) {
		this.bcn_outboxcode = bcn_outboxcode;
	}

	public Long getBcn_outboxid() {
		return bcn_outboxid;
	}

	public void setBcn_outboxid(Long bcn_outboxid) {
		this.bcn_outboxid = bcn_outboxid;
	}

	public String getBcn_status() {
		return bcn_status;
	}

	public void setBcn_status(String bcn_status) {
		this.bcn_status = bcn_status;
	}

	public String toDelete() {
		return "delete from barAcceptNotify where ban_anid=" + this.getBcn_anid();
	}

	public String toSqlOutSource(int primaryKey) {
		return "insert into baracceptnotify (ban_id, ban_barcode, ban_anid, ban_ancode, ban_andid, ban_anddetno, ban_prodcode, "
				+ "ban_prodid, ban_qty, ban_vendcode, ban_outboxcode, ban_outboxid, ban_status)" + " values ("
				+ primaryKey + ", '" + this.bcn_barcode + "', " + this.bcn_anid + ", '" + this.bcn_ancode + "', "
				+ this.bcn_andid + ", " + this.bcn_anddetno + ", '" + this.bcn_prodcode + "', " + this.bcn_prodid + ", "
				+ this.bcn_qty + ", " + this.bcn_vendcode + ", '" + this.bcn_outboxcode + "', " + this.bcn_outboxid
				+ ", '" + this.bcn_status + "'" + ")";
	}

}
