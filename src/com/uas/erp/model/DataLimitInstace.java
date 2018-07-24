package com.uas.erp.model;

import java.io.Serializable;

public class DataLimitInstace implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String table_;
	private String desc_;
	private String codefield_;
	private String namefield_;
	private Integer empid_;
	private Integer jobid_;
	private int nolimit_;
	private int noaddlimit_;
	private String usereport_;
	private int parentid_;
	private String em_code;
	private String em_name;
	private int instanceid_;
	private String limittype_;
	private String condition_;
	public String getTable_() {
		return table_;
	}
	public void setTable_(String table_) {
		this.table_ = table_;
	}
	public String getDesc_() {
		return desc_;
	}
	public void setDesc_(String desc_) {
		this.desc_ = desc_;
	}
	public String getCodefield_() {
		return codefield_;
	}
	public void setCodefield_(String codefield_) {
		this.codefield_ = codefield_;
	}
	public String getNamefield_() {
		return namefield_;
	}
	public void setNamefield_(String namefield_) {
		this.namefield_ = namefield_;
	}
	public Integer getEmpid_() {
		return empid_;
	}
	public void setEmpid_(Integer empid_) {
		this.empid_ = empid_;
	}
	public int getNolimit_() {
		return nolimit_;
	}
	public void setNolimit_(int nolimit_) {
		this.nolimit_ = nolimit_;
	}
	public int getNoaddlimit_() {
		return noaddlimit_;
	}
	public void setNoaddlimit_(int noaddlimit_) {
		this.noaddlimit_ = noaddlimit_;
	}
	public String getUsereport_() {
		return usereport_;
	}
	public void setUsereport_(String usereport_) {
		this.usereport_ = usereport_;
	}
	public int getParentid_() {
		return parentid_;
	}
	public void setParentid_(int parentid_) {
		this.parentid_ = parentid_;
	}
	public String getEm_code() {
		return em_code;
	}
	public void setEm_code(String em_code) {
		this.em_code = em_code;
	}
	public String getEm_name() {
		return em_name;
	}
	public void setEm_name(String em_name) {
		this.em_name = em_name;
	}
	public int getInstanceid_() {
		return instanceid_;
	}
	public void setInstanceid_(int instanceid_) {
		this.instanceid_ = instanceid_;
	}
	public String getLimittype_() {
		return limittype_;
	}
	public void setLimittype_(String limittype_) {
		this.limittype_ = limittype_;
	}
	public String getCondition_() {
		return condition_;
	}
	public void setCondition_(String condition_) {
		this.condition_ = condition_;
	}
	public Integer getJobid_() {
		return jobid_;
	}
	public void setJobid_(Integer jobid_) {
		this.jobid_ = jobid_;
	}
	
}
