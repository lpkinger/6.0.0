package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.KeyEntity;

/**
 * 买家ERP系统的采购询价单
 * 
 * @author yingp
 * 
 */
public class Inquiry extends KeyEntity {

	private Long in_id;
	private String in_code;
	private Date in_date;
	private String in_recorder;
	private Long in_recorderuu;
	private String in_auditor;
	private Date in_recorddate;
	private Date in_enddate;
	private String in_remark;
	private String in_kind;
	private String in_environment;//环保要求
	private List<InquiryDetail> details;
	private String in_attach;
	private List<Attach> attaches;// 所有的附件信息
	private String in_pricetype;
	// 主动报价用字段
	private Long b2b_qu_id;
	private Long in_buyeruu;

	public Long getIn_id() {
		return in_id;
	}

	public void setIn_id(Long in_id) {
		this.in_id = in_id;
	}

	public String getIn_code() {
		return in_code;
	}

	public void setIn_code(String in_code) {
		this.in_code = in_code;
	}

	public Date getIn_date() {
		return in_date;
	}

	public void setIn_date(Date in_date) {
		this.in_date = in_date;
	}

	public String getIn_recorder() {
		return in_recorder;
	}

	public void setIn_recorder(String in_recorder) {
		this.in_recorder = in_recorder;
	}

	public String getIn_auditor() {
		return in_auditor;
	}

	public void setIn_auditor(String in_auditor) {
		this.in_auditor = in_auditor;
	}

	public String getIn_pricetype() {
		return in_pricetype;
	}

	public void setIn_priceType(String in_pricetype) {
		this.in_pricetype = in_pricetype;
	}

	public Date getIn_recorddate() {
		return in_recorddate;
	}

	public void setIn_recorddate(Date in_recorddate) {
		this.in_recorddate = in_recorddate;
	}

	public Date getIn_enddate() {
		return in_enddate;
	}

	public void setIn_enddate(Date in_enddate) {
		this.in_enddate = in_enddate;
	}

	public String getIn_remark() {
		return in_remark;
	}

	public void setIn_remark(String in_remark) {
		this.in_remark = in_remark;
	}

	public String getIn_attach() {
		return in_attach;
	}

	public void setIn_attach(String in_attach) {
		this.in_attach = in_attach;
	}

	public List<InquiryDetail> getDetails() {
		return details;
	}

	public void setDetails(List<InquiryDetail> details) {
		this.details = details;
	}

	public Long getB2b_qu_id() {
		return b2b_qu_id;
	}

	public void setB2b_qu_id(Long b2b_qu_id) {
		this.b2b_qu_id = b2b_qu_id;
	}

	public Long getIn_buyeruu() {
		return in_buyeruu;
	}

	public void setIn_buyeruu(Long in_buyeruu) {
		this.in_buyeruu = in_buyeruu;
	}

	public Long getIn_recorderuu() {
		return in_recorderuu;
	}

	public void setIn_recorderuu(Long in_recorderuu) {
		this.in_recorderuu = in_recorderuu;
	}

	public String getIn_environment() {
		return in_environment;
	}

	public void setIn_environment(String in_environment) {
		this.in_environment = in_environment;
	}

	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}

	public void setIn_pricetype(String in_pricetype) {
		this.in_pricetype = in_pricetype;
	}

	public String getIn_kind() {
		return in_kind;
	}

	public void setIn_kind(String in_kind) {
		this.in_kind = in_kind;
	}

	/**
	 * 主动报价SQL封装
	 * 
	 * @param code
	 * @param primaryKey
	 * @return
	 */
	public String toSqlString(String code, int primaryKey) {
		return "insert into Inquiry(in_id,in_code,in_auditdate,in_source,in_environment,b2b_qu_id,in_date,in_recorddate,in_enddate,in_remark,in_sendstatus,in_class,in_status,in_statuscode) values ("
				+ primaryKey
				+ ",'"
				+ code
				+ "',sysdate,'"
				+ this.in_code
				+ "','"
				+ this.in_environment
				+ "',"
				+ this.b2b_qu_id
				+ ","
				+ DateUtil.parseDateToOracleString(null, in_date)
				+ ","
				+ DateUtil.parseDateToOracleString(null, in_recorddate)
				+ ","
				+ DateUtil.parseDateToOracleString(null, in_enddate)
				+ ",'"
				+ StringUtil.nvl(in_remark, "")
				+ "','已上传','主动报价','"
				+ Status.AUDITED.display() + "','" + Status.AUDITED.code() + "')";
	}

	@Override
	public Object getKey() {
		return this.in_id;
	}

}
