package com.uas.erp.model;

import java.util.Date;

public class ScheduleTask {

	private String bean_;
	private String function_;
	private String cron_;
	private String man_;
	private Date date_;
	private Integer enable_;
	private String remark_;
	private Integer id_;
	private String master_;
	private String condition_;
	
	public String getBean_() {
		return bean_;
	}
	public void setBean_(String bean_) {
		this.bean_ = bean_;
	}
	public String getFunction_() {
		return function_;
	}
	public void setFunction_(String function_) {
		this.function_ = function_;
	}
	public String getCron_() {
		return cron_;
	}
	public void setCron_(String cron_) {
		this.cron_ = cron_;
	}
	public String getMan_() {
		return man_;
	}
	public void setMan_(String man_) {
		this.man_ = man_;
	}
	public Date getDate_() {
		return date_;
	}
	public void setDate_(Date date_) {
		this.date_ = date_;
	}
	
	public Integer getEnable_() {
		return enable_;
	}
	public void setEnable_(Integer enable_) {
		this.enable_ = enable_;
	}
	public String getRemark_() {
		return remark_;
	}
	public void setRemark_(String remark_) {
		this.remark_ = remark_;
	}
	
	
	public Integer getId_() {
		return id_;
	}
	public void setId_(Integer id_) {
		this.id_ = id_;
	}
	public String getMaster_() {
		return master_;
	}
	public void setMaster_(String master_) {
		this.master_ = master_;
	}
	public String getCondition_() {
		return condition_;
	}
	public void setCondition_(String condition_) {
		this.condition_ = condition_;
	}
	public ScheduleTask() {
		super();
	}
	public ScheduleTask(String bean_, String function_, String cron_, String man_, Date date_, Integer enable_,
			String remark_, Integer id_, String master_, String condition_) {
		super();
		this.bean_ = bean_;
		this.function_ = function_;
		this.cron_ = cron_;
		this.man_ = man_;
		this.date_ = date_;
		this.enable_ = enable_;
		this.remark_ = remark_;
		this.id_ = id_;
		this.master_ = master_;
		this.condition_ = condition_;
	}
	@Override
	public String toString() {
		return "ScheduleTask [bean_=" + bean_ + ", function_=" + function_ + ", cron_=" + cron_ + ", man_=" + man_
				+ ", date_=" + date_ + ", enable_=" + enable_ + ", remark_=" + remark_ + ", id_=" + id_ + ", master_="
				+ master_ + ", condition_=" + condition_ + "]";
	}
}
