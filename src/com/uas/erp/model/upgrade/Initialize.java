package com.uas.erp.model.upgrade;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的initialize
 * 
 * @author yingp
 *
 */
public class Initialize implements Saveable{

	private Integer in_id;// ID
	private Integer in_pid;// 父节点ID
	private Short in_leaf;// 是否叶节点
	private String in_img;// 关联图片
	private String in_caller;// 关联caller、表名
	private String in_desc;// 描述
	private String in_url;// 关联页面
	private String in_description;// 备注信息
	private String in_flowcaller;
	private Short in_isrequired;
	private String in_man;
	private Date in_updatedate;
	private Short in_detno;
	private String in_code;
	private Short in_ismoudle;
	private Short in_isenabled;
	
	private String plan_id;

	public Integer getIn_id() {
		return in_id;
	}

	public void setIn_id(Integer in_id) {
		this.in_id = in_id;
	}

	public Integer getIn_pid() {
		return in_pid;
	}

	public void setIn_pid(Integer in_pid) {
		this.in_pid = in_pid;
	}

	public Short getIn_leaf() {
		return in_leaf;
	}

	public void setIn_leaf(Short in_leaf) {
		this.in_leaf = in_leaf;
	}

	public String getIn_img() {
		return in_img;
	}

	public void setIn_img(String in_img) {
		this.in_img = in_img;
	}

	public String getIn_caller() {
		return in_caller;
	}

	public void setIn_caller(String in_caller) {
		this.in_caller = in_caller;
	}

	public String getIn_desc() {
		return in_desc;
	}

	public void setIn_desc(String in_desc) {
		this.in_desc = in_desc;
	}

	public String getIn_url() {
		return in_url;
	}

	public void setIn_url(String in_url) {
		this.in_url = in_url;
	}

	public String getIn_description() {
		return in_description;
	}

	public void setIn_description(String in_description) {
		this.in_description = in_description;
	}

	public String getIn_flowcaller() {
		return in_flowcaller;
	}

	public void setIn_flowcaller(String in_flowcaller) {
		this.in_flowcaller = in_flowcaller;
	}

	public Short getIn_isrequired() {
		return in_isrequired;
	}

	public void setIn_isrequired(Short in_isrequired) {
		this.in_isrequired = in_isrequired;
	}

	public String getIn_man() {
		return in_man;
	}

	public void setIn_man(String in_man) {
		this.in_man = in_man;
	}

	public Date getIn_updatedate() {
		return in_updatedate;
	}

	public void setIn_updatedate(Date in_updatedate) {
		this.in_updatedate = in_updatedate;
	}

	public Short getIn_detno() {
		return in_detno;
	}

	public void setIn_detno(Short in_detno) {
		this.in_detno = in_detno;
	}

	public String getIn_code() {
		return in_code;
	}

	public void setIn_code(String in_code) {
		this.in_code = in_code;
	}

	public Short getIn_ismoudle() {
		return in_ismoudle;
	}

	public void setIn_ismoudle(Short in_ismoudle) {
		this.in_ismoudle = in_ismoudle;
	}

	public Short getIn_isenabled() {
		return in_isenabled;
	}

	public void setIn_isenabled(Short in_isenabled) {
		this.in_isenabled = in_isenabled;
	}

	@JsonIgnore
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	@Override
	public String table() {
		return "upgrade$initialize";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
