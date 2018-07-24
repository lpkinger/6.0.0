package com.uas.erp.model;

import java.io.Serializable;

/**
 * 审批节点关系表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowBoxRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fbr_id;// ID
	private int fbr_flowid;// 实例流程ID
	private int fbr_boxid;// 实例节点ID
	private String fbr_boxname;// 实例节点名称
	private int fbr_subofid;// 父节点ID
	private String fbr_subofname;// 父节点名称
	private String fbr_type;// 是否必须执行才往下流转，同节点关系

	public int getFbr_id() {
		return fbr_id;
	}

	public void setFbr_id(int fbr_id) {
		this.fbr_id = fbr_id;
	}

	public int getFbr_flowid() {
		return fbr_flowid;
	}

	public void setFbr_flowid(int fbr_flowid) {
		this.fbr_flowid = fbr_flowid;
	}

	public int getFbr_boxid() {
		return fbr_boxid;
	}

	public void setFbr_boxid(int fbr_boxid) {
		this.fbr_boxid = fbr_boxid;
	}

	public String getFbr_subofname() {
		return fbr_subofname;
	}

	public void setFbr_subofname(String fbr_subofname) {
		this.fbr_subofname = fbr_subofname;
	}

	public String getFbr_boxname() {
		return fbr_boxname;
	}

	public void setFbr_boxname(String fbr_boxname) {
		this.fbr_boxname = fbr_boxname;
	}

	public int getFbr_subofid() {
		return fbr_subofid;
	}

	public void setFbr_subofid(int fbr_subofid) {
		this.fbr_subofid = fbr_subofid;
	}

	public String getFbr_type() {
		return fbr_type;
	}

	public void setFbr_type(String fbr_type) {
		this.fbr_type = fbr_type;
	}
}
