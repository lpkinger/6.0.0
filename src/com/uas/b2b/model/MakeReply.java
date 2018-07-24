package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;

/**
 * 委外加工单明细回复记录
 * 
 * @author suntg
 * @since 2015年8月5日14:04:17
 * 
 */
public class MakeReply {

	private Integer mr_id;
	private Double mr_qty;
	private Date mr_delivery;
	private String mr_remark;
	private String mr_macode;
	private Date mr_date;
	private String mr_recorder;
	private Long b2b_mr_id;// 平台里面的ID，作为唯一标志，防止重复写入回复记录
	private String mr_type;
	
	public Integer getMr_id() {
		return mr_id;
	}
	public void setMr_id(Integer mr_id) {
		this.mr_id = mr_id;
	}
	public Double getMr_qty() {
		return mr_qty;
	}
	public void setMr_qty(Double mr_qty) {
		this.mr_qty = mr_qty;
	}
	public Date getMr_delivery() {
		return mr_delivery;
	}
	public void setMr_delivery(Date mr_delivery) {
		this.mr_delivery = mr_delivery;
	}
	public String getMr_remark() {
		return mr_remark;
	}
	public void setMr_remark(String mr_remark) {
		this.mr_remark = mr_remark;
	}
	public String getMr_macode() {
		return mr_macode;
	}
	public void setMr_macode(String mr_macode) {
		this.mr_macode = mr_macode;
	}
	public Date getMr_date() {
		return mr_date;
	}
	public void setMr_date(Date mr_date) {
		this.mr_date = mr_date;
	}
	public String getMr_recorder() {
		return mr_recorder;
	}
	public void setMr_recorder(String mr_recorder) {
		this.mr_recorder = mr_recorder;
	}
	public Long getB2b_mr_id() {
		return b2b_mr_id;
	}
	public void setB2b_mr_id(Long b2b_mr_id) {
		this.b2b_mr_id = b2b_mr_id;
	}
	public String getMr_type() {
		return mr_type;
	}
	public void setMr_type(String mr_type) {
		this.mr_type = mr_type;
	}
	
	public String toSqlString(){
		return "insert into makereply (mr_id, mr_qty, mr_delivery, mr_date, mr_remark, mr_macode, mr_recorder, mr_type, mr_sendstatus, b2b_mr_id) values ("
				+ "makereply_seq.nextval"
				+ ", "
				+ NumberUtil.nvl(mr_qty, 0)
				+ ", "
				+ DateUtil.parseDateToOracleString(null, mr_delivery)
				+ ", "
				+ DateUtil.parseDateToOracleString(null, mr_date)
				+ ", '"
				+ StringUtil.nvl(mr_remark, "")
				+ "', '"
				+ StringUtil.nvl(mr_macode, "")
				+ "', '"
				+ StringUtil.nvl(mr_recorder, "")
				+ "', '"
				+ StringUtil.nvl(mr_type, "")
				+ "', '"
				+ "已上传"
				+ "', "
				+ b2b_mr_id
				+ ")";
	}


}
