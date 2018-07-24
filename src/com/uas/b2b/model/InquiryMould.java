package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import com.uas.erp.core.support.KeyEntity;

/**
 * 买家ERP系统的模具询价单
 * 
 * @author hejq
 * 
 */
public class InquiryMould extends KeyEntity {

	/**
	 * id
	 */
	private Long in_id;

	/**
	 * 询价单号
	 */
	private String in_code;

	/**
	 * 日期
	 */
	private Date in_date;

	/**
	 * 供应商号
	 */
	private String in_vendcode;

	/**
	 * 供应商名
	 */
	private String in_vendname;

	/**
	 * 供应商UU
	 */
	private Long in_veuu;

	/**
	 * 币别
	 */
	private String in_currency;

	/**
	 * 税率
	 */
	private Float in_taxrate;

	/**
	 * 备注
	 */
	private String in_remark;

	/**
	 * 单据状态
	 */
	private String in_status;

	/**
	 * 所属公司
	 */
	private String in_cop;

	/**
	 * 来源id
	 */
	private Long in_sourceid;

	/**
	 * 来源单号
	 */
	private String in_sourcecode;

	/**
	 * 来源类型
	 */
	private String in_sourcetype;

	/**
	 * 状态编号
	 */
	private String in_statuscode;

	/**
	 * 审核人
	 */
	private String in_auditor;

	/**
	 * 审核日期
	 */
	private Date in_auditdate;

	/**
	 * 录入人
	 */
	private String in_recorder;

	/**
	 * 录入人id
	 */
	private Long in_recorderid;

	/**
	 * 录入日期
	 */
	private Date in_recorddate;

	/**
	 * 截止日期
	 */
	private Date in_enddate;

	/**
	 * 采纳状态
	 */
	private String in_adoptstatus;

	/**
	 * 上传状态
	 */
	private String in_sendstatus;

	/**
	 * 附件
	 */
	private String in_attach;

	/**
	 * 模具询价明细
	 */
	private List<InquiryMouldDet> items;

	/**
	 * 审核状态
	 * 
	 * @return
	 */
	private String in_checksendStatus;

	/**
	 * b2b对应id
	 */
	private Long b2b_im_id;

	private List<Attach> attaches;

	/**
	 * 是否为第一次报价，true为是，false为修改报价
	 */
	private boolean isSave;

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

	public String getIn_vendcode() {
		return in_vendcode;
	}

	public void setIn_vendcode(String in_vendcode) {
		this.in_vendcode = in_vendcode;
	}

	public String getIn_vendname() {
		return in_vendname;
	}

	public void setIn_vendname(String in_vendname) {
		this.in_vendname = in_vendname;
	}

	public Long getIn_veuu() {
		return in_veuu;
	}

	public void setIn_veuu(Long in_veuu) {
		this.in_veuu = in_veuu;
	}

	public String getIn_currency() {
		return in_currency;
	}

	public void setIn_currency(String in_currency) {
		this.in_currency = in_currency;
	}

	public Float getIn_tax() {
		return in_taxrate;
	}

	public void setIn_tax(Float in_taxrate) {
		this.in_taxrate = in_taxrate;
	}

	public String getIn_remark() {
		return in_remark;
	}

	public void setIn_remark(String in_remark) {
		this.in_remark = in_remark;
	}

	public String getIn_status() {
		return in_status;
	}

	public void setIn_status(String in_status) {
		this.in_status = in_status;
	}

	public String getIn_cop() {
		return in_cop;
	}

	public void setIn_cop(String in_cop) {
		this.in_cop = in_cop;
	}

	public Long getIn_sourceid() {
		return in_sourceid;
	}

	public void setIn_sourceid(Long in_sourceid) {
		this.in_sourceid = in_sourceid;
	}

	public String getIn_sourcecode() {
		return in_sourcecode;
	}

	public void setIn_sourcecode(String in_sourcecode) {
		this.in_sourcecode = in_sourcecode;
	}

	public String getIn_sourcetype() {
		return in_sourcetype;
	}

	public void setIn_sourcetype(String in_sourcetype) {
		this.in_sourcetype = in_sourcetype;
	}

	public String getIn_statuscode() {
		return in_statuscode;
	}

	public void setIn_statuscode(String in_statuscode) {
		this.in_statuscode = in_statuscode;
	}

	public String getIn_auditor() {
		return in_auditor;
	}

	public void setIn_auditor(String in_auditor) {
		this.in_auditor = in_auditor;
	}

	public Date getIn_auditdate() {
		return in_auditdate;
	}

	public void setIn_auditdate(Date in_auditdate) {
		this.in_auditdate = in_auditdate;
	}

	public String getIn_recorder() {
		return in_recorder;
	}

	public void setIn_recorder(String in_recorder) {
		this.in_recorder = in_recorder;
	}

	public Long getIn_recorderid() {
		return in_recorderid;
	}

	public void setIn_recorderid(Long in_recorderid) {
		this.in_recorderid = in_recorderid;
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

	public String getIn_adoptstatus() {
		return in_adoptstatus;
	}

	public void setIn_adoptstatus(String in_adopistatus) {
		this.in_adoptstatus = in_adopistatus;
	}

	public String getIn_sendstatus() {
		return in_sendstatus;
	}

	public void setIn_sendstatus(String in_sendstatus) {
		this.in_sendstatus = in_sendstatus;
	}

	public String getIn_attach() {
		return in_attach;
	}

	public void setIn_attach(String in_attach) {
		this.in_attach = in_attach;
	}

	public List<InquiryMouldDet> getItems() {
		return items;
	}

	public void setItems(List<InquiryMouldDet> items) {
		this.items = items;
	}

	public String getIn_checksendStatus() {
		return in_checksendStatus;
	}

	public void setIn_checksendStatus(String in_checksendStatus) {
		this.in_checksendStatus = in_checksendStatus;
	}

	public Long getB2b_im_id() {
		return b2b_im_id;
	}

	public void setB2b_im_id(Long b2b_im_id) {
		this.b2b_im_id = b2b_im_id;
	}

	public List<Attach> getAttaches() {
		return attaches;
	}

	public void setAttaches(List<Attach> attaches) {
		this.attaches = attaches;
	}

	public Float getIn_taxrate() {
		return in_taxrate;
	}

	public void setIn_taxrate(Float in_taxrate) {
		this.in_taxrate = in_taxrate;
	}

	public boolean isSave() {
		return isSave;
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	@Override
	public Object getKey() {
		return this.in_id;
	}

}
