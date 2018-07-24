package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.core.support.KeyEntity;

public class MakeChange extends KeyEntity {

	private Long mc_id;// id
	private String mc_code;// 变更单号
	private Date mc_indate;// 录入日期
	private Date mc_date;// 日期
	private String mc_recorder;// 录入人
	private String mc_auditman;// 审核人
	private Date mc_auditdate;// 审核日期
	private String mc_remark;// 备注
	private List<MakeChangeDetail> changeDetails;// 明细

	@JsonIgnore
	public Long getMc_id() {
		return mc_id;
	}

	public void setMc_id(Long mc_id) {
		this.mc_id = mc_id;
	}

	public String getMc_code() {
		return mc_code;
	}

	public void setMc_code(String mc_code) {
		this.mc_code = mc_code;
	}

	public Date getMc_indate() {
		return mc_indate;
	}

	public void setMc_indate(Date mc_indate) {
		this.mc_indate = mc_indate;
	}

	public Date getMc_date() {
		return mc_date;
	}

	public void setMc_date(Date mc_date) {
		this.mc_date = mc_date;
	}

	public String getMc_recorder() {
		return mc_recorder;
	}

	public void setMc_recorder(String mc_recorder) {
		this.mc_recorder = mc_recorder;
	}

	public String getMc_auditman() {
		return mc_auditman;
	}

	public void setMc_auditman(String mc_auditman) {
		this.mc_auditman = mc_auditman;
	}

	public Date getMc_auditdate() {
		return mc_auditdate;
	}

	public void setMc_auditdate(Date mc_auditdate) {
		this.mc_auditdate = mc_auditdate;
	}

	public String getMc_remark() {
		return mc_remark;
	}

	public void setMc_remark(String mc_remark) {
		this.mc_remark = mc_remark;
	}

	public List<MakeChangeDetail> getChangeDetails() {
		return changeDetails;
	}

	public void setChangeDetails(List<MakeChangeDetail> changeDetails) {
		this.changeDetails = changeDetails;
	}

	/**
	 * 获取主键值
	 * @see com.uas.erp.core.support.KeyEntity#getKey()
	 */
	@Override
	public Object getKey() {
		return this.mc_id;
	}

}
