package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

public class SaleForecastDownDet {
	private Long b2b_id;
	private Short pfd_detno;// 序号 Group12--LIN
	private String pfd_custprodcode;// 客户物料编号Group12-GIN
	private String pfd_custproddetail;// 客户物料名称Group22-NAD
	private String pfd_custprodspec;// 客户规格Group20-PAC
	private String pfd_custprodunit;// 客户单位Group20-MEA
	private Date pfd_delivery;// 交货日期Group18-DTM
	private Double pfd_qty;// 数量 Group18-QTY
	private String pfd_remark;// 备注 Group12-FTX
	private Long pfd_useruu;// 卖方业务员uu号

	public Short getPfd_detno() {
		return pfd_detno;
	}

	public void setPfd_detno(Short pfd_detno) {
		this.pfd_detno = pfd_detno;
	}

	public String getPfd_custprodcode() {
		return pfd_custprodcode;
	}

	public void setPfd_custprodcode(String pfd_custprodcode) {
		this.pfd_custprodcode = pfd_custprodcode;
	}

	public String getPfd_custproddetail() {
		return pfd_custproddetail;
	}

	public void setPfd_custproddetail(String pfd_custproddetail) {
		this.pfd_custproddetail = pfd_custproddetail;
	}

	public String getPfd_custprodspec() {
		return pfd_custprodspec;
	}

	public void setPfd_custprodspec(String pfd_custprodspec) {
		this.pfd_custprodspec = pfd_custprodspec;
	}

	public String getPfd_custprodunit() {
		return pfd_custprodunit;
	}

	public void setPfd_custprodunit(String pfd_custprodunit) {
		this.pfd_custprodunit = pfd_custprodunit;
	}

	public Date getPfd_delivery() {
		return pfd_delivery;
	}

	public void setPfd_delivery(Date pfd_delivery) {
		this.pfd_delivery = pfd_delivery;
	}

	public Double getPfd_qty() {
		return pfd_qty;
	}

	public void setPfd_qty(Double pfd_qty) {
		this.pfd_qty = pfd_qty;
	}

	public String getPfd_remark() {
		return pfd_remark;
	}

	public void setPfd_remark(String pfd_remark) {
		this.pfd_remark = pfd_remark;
	}

	public Long getPfd_useruu() {
		return pfd_useruu;
	}

	public void setPfd_useruu(Long pfd_useruu) {
		this.pfd_useruu = pfd_useruu;
	}

	public Long getB2b_id() {
		return b2b_id;
	}

	public void setB2b_id(Long b2b_id) {
		this.b2b_id = b2b_id;
	}

	public String toSqlString(int foreignKey) {
		return "insert into purchaseforecastDowndet(pfd_id,pfd_pfid,pfd_detno,pfd_custprodcode,pfd_custproddetail,pfd_custprodspec,pfd_custprodunit,pfd_delivery,pfd_qty,PFD_REMARK) VALUES ("
				+ "PurchaseforecastDownDet_SEQ.nextval," + foreignKey + "," + pfd_detno + ",'" + pfd_custprodcode
				+ "','" + StringUtil.nvl(pfd_custproddetail, "") + "','" + StringUtil.nvl(pfd_custprodspec, "") + "','"
				+ StringUtil.nvl(pfd_custprodunit, "") + "',"
				+ (pfd_delivery == null ? "null" : DateUtil.parseDateToOracleString(null, pfd_delivery)) + ","
				+ NumberUtil.nvl(pfd_qty, 0) + ",'" + StringUtil.nvl(pfd_remark, "") + "'" + ")";
	}
}
