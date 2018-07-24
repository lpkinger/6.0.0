package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;

/**
 * erp package模型
 * 
 * @author aof
 * @date 2015年10月28日
 */
public class PackageDown {
	private Long pa_id;// 记录B2b上传表的id号
	private String pa_prodcode;// 物料编号
	private String pa_outboxcode;// 包装编号/条码
	private Date pa_packdate;// 打包日期
	private Long pa_level;// 包装层级
	private Long pa_packageqty;// 箱内件数
	private Double pa_totalqty;// 箱外总数
	private String pa_makecode;// 制造单号
	private String pa_status;// 状态
	private Date pa_indate; // 录入日期
	private List<PackageDownDetail> details;

	public Long getPa_id() {
		return pa_id;
	}

	public void setPa_id(Long pa_id) {
		this.pa_id = pa_id;
	}

	public String getPa_prodcode() {
		return pa_prodcode;
	}

	public void setPa_prodcode(String pa_prodcode) {
		this.pa_prodcode = pa_prodcode;
	}

	public String getPa_outboxcode() {
		return pa_outboxcode;
	}

	public void setPa_outboxcode(String pa_outboxcode) {
		this.pa_outboxcode = pa_outboxcode;
	}

	public Date getPa_packdate() {
		return pa_packdate;
	}

	public void setPa_packdate(Date pa_packdate) {
		this.pa_packdate = pa_packdate;
	}

	public Long getPa_level() {
		return pa_level;
	}

	public void setPa_level(Long pa_level) {
		this.pa_level = pa_level;
	}

	public Long getPa_packageqty() {
		return pa_packageqty;
	}

	public void setPa_packageqty(Long pa_packageqty) {
		this.pa_packageqty = pa_packageqty;
	}

	public Double getPa_totalqty() {
		return pa_totalqty;
	}

	public void setPa_totalqty(Double pa_totalqty) {
		this.pa_totalqty = pa_totalqty;
	}

	public String getPa_makecode() {
		return pa_makecode;
	}

	public void setPa_makecode(String pa_makecode) {
		this.pa_makecode = pa_makecode;
	}

	public String getPa_status() {
		return pa_status;
	}

	public void setPa_status(String pa_status) {
		this.pa_status = pa_status;
	}

	public Date getPa_indate() {
		return pa_indate;
	}

	public void setPa_indate(Date pa_indate) {
		this.pa_indate = pa_indate;
	}

	public List<PackageDownDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PackageDownDetail> details) {
		this.details = details;
	}

	public String toDelete() {
		return "delete from package where pa_b2bid =" + this.pa_id;
	}

	public String toSqlString(int primaryKey) {
		return "insert into package (pa_id, pa_b2bid,pa_prodcode, pa_outboxcode, pa_packdate, pa_level, pa_packageqty, pa_totalqty, pa_makecode, pa_status, pa_indate)"
				+ " values (" + primaryKey + ", " + this.pa_id + ", '" + this.pa_prodcode + "', '" + this.pa_outboxcode
				+ "', " + DateUtil.parseDateToOracleString(null, this.pa_packdate) + ", " + this.pa_level + ", "
				+ this.pa_packageqty + ", " + this.pa_totalqty + ", '" + this.pa_makecode + "', '" + this.pa_status
				+ "', " + DateUtil.parseDateToOracleString(null, this.pa_indate) + ")";
	}

}
