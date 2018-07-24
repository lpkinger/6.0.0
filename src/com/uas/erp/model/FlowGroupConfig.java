package com.uas.erp.model;

public class FlowGroupConfig {

	private Integer fgc_id;					//ID
	private String fgc_groupName;			//分组名称
	private String fgc_field;				//字段名称
	private Integer fgc_new;				//是否全新字段
	private Integer fgc_requiredField;		//是否必填
	private Integer fgc_read;				//是否只读
	private Integer fgc_remark;				//是否备注
	private String fgc_fdShortName;			//版本简称
	private Integer fgc_detno;				//序号
	private Integer fgc_width;				//宽度
	private String fgc_role;				//角色
	private String fgc_roleCode;			//角色编号
	
	public Integer getFgc_id() {
		return fgc_id;
	}
	public void setFgc_id(Integer fgc_id) {
		this.fgc_id = fgc_id;
	}
	public String getFgc_field() {
		return fgc_field;
	}
	public void setFgc_field(String fgc_field) {
		this.fgc_field = fgc_field;
	}
	public Integer getFgc_new() {
		return fgc_new;
	}
	public void setFgc_new(Integer fgc_new) {
		this.fgc_new = fgc_new;
	}
	public Integer getFgc_requiredField() {
		return fgc_requiredField;
	}
	public void setFgc_requiredField(Integer fgc_requiredField) {
		this.fgc_requiredField = fgc_requiredField;
	}
	public Integer getFgc_read() {
		return fgc_read;
	}
	public void setFgc_read(Integer fgc_read) {
		this.fgc_read = fgc_read;
	}
	public Integer getFgc_remark() {
		return fgc_remark;
	}
	public void setFgc_remark(Integer fgc_remark) {
		this.fgc_remark = fgc_remark;
	}
	public Integer getFgc_detno() {
		return fgc_detno;
	}
	public void setFgc_detno(Integer fgc_detno) {
		this.fgc_detno = fgc_detno;
	}
	public Integer getFgc_width() {
		return fgc_width;
	}
	public void setFgc_width(Integer fgc_width) {
		this.fgc_width = fgc_width;
	}
	public String getFgc_role() {
		return fgc_role;
	}
	public void setFgc_role(String fgc_role) {
		this.fgc_role = fgc_role;
	}
	public String getFgc_groupName() {
		return fgc_groupName;
	}
	public void setFgc_groupName(String fgc_groupName) {
		this.fgc_groupName = fgc_groupName;
	}
	public String getFgc_fdShortName() {
		return fgc_fdShortName;
	}
	public void setFgc_fdShortName(String fgc_fdShortName) {
		this.fgc_fdShortName = fgc_fdShortName;
	}
	public String getFgc_roleCode() {
		return fgc_roleCode;
	}
	public void setFgc_roleCode(String fgc_roleCode) {
		this.fgc_roleCode = fgc_roleCode;
	}
	
}
