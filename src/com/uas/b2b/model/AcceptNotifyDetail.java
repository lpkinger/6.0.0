package com.uas.b2b.model;

import com.uas.erp.core.StringUtil;

/**
 * 收料通知单明细
 * 
 * @author yingp
 * 
 */
public class AcceptNotifyDetail {

	private short and_detno;
	private String and_ordercode;
	private Short and_orderdetno;
	private Long and_pnid;
	private Double and_inqty;
	private Double and_price;
	private String and_remark;

	public short getAnd_detno() {
		return and_detno;
	}

	public void setAnd_detno(short and_detno) {
		this.and_detno = and_detno;
	}

	public String getAnd_ordercode() {
		return and_ordercode;
	}

	public void setAnd_ordercode(String and_ordercode) {
		this.and_ordercode = and_ordercode;
	}

	public Short getAnd_orderdetno() {
		return and_orderdetno;
	}

	public void setAnd_orderdetno(Short and_orderdetno) {
		this.and_orderdetno = and_orderdetno;
	}

	public Double getAnd_inqty() {
		return and_inqty;
	}

	public void setAnd_inqty(Double and_inqty) {
		this.and_inqty = and_inqty;
	}

	public Long getAnd_pnid() {
		return and_pnid;
	}

	public void setAnd_pnid(Long and_pnid) {
		this.and_pnid = and_pnid;
	}

	public String getAnd_remark() {
		return and_remark;
	}

	public void setAnd_remark(String and_remark) {
		this.and_remark = and_remark;
	}

	public Double getAnd_price() {
		return and_price;
	}

	public void setAnd_price(Double and_price) {
		this.and_price = and_price;
	}

	public String toSqlString(int foreignKey) {
		return "insert into AcceptNotifyDetail(and_id,and_anid,and_detno,and_ordercode,and_orderdetno,and_inqty,and_b2bqty,and_remark,and_price,and_pnid) values (AcceptNotifyDetail_seq.nextval,"
				+ foreignKey
				+ ","
				+ and_detno
				+ ",'"
				+ and_ordercode
				+ "',"
				+ and_orderdetno
				+ ","
				+ and_inqty
				+ ","
				+ and_inqty
				+ ",'"
				+ StringUtil.nvl(and_remark, "") + "'," + and_price + "," + this.and_pnid + ")";
	}

}
