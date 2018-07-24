package com.uas.erp.model;

import java.io.Serializable;

/**
 * 流程关系表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowNodeRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fnr_id;// ID
	private int fnr_fiid;// 流程ID
	private int fnr_nodeid;// 节点ID
	private int fnr_subofid;// 父节点ID
	private String fnr_subofname;// 父节点名称
	private String fnr_type;// 节点类型（and、or）类型为知会的值为or，其它为and

	public int getFnr_id() {
		return fnr_id;
	}

	public void setFnr_id(int fnr_id) {
		this.fnr_id = fnr_id;
	}

	public int getFnr_fiid() {
		return fnr_fiid;
	}

	public void setFnr_fiid(int fnr_fiid) {
		this.fnr_fiid = fnr_fiid;
	}

	public int getFnr_nodeid() {
		return fnr_nodeid;
	}

	public void setFnr_nodeid(int fnr_nodeid) {
		this.fnr_nodeid = fnr_nodeid;
	}

	public int getFnr_subofid() {
		return fnr_subofid;
	}

	public void setFnr_subofid(int fnr_subofid) {
		this.fnr_subofid = fnr_subofid;
	}

	public String getFnr_subofname() {
		return fnr_subofname;
	}

	public void setFnr_subofname(String fnr_subofname) {
		this.fnr_subofname = fnr_subofname;
	}

	public String getFnr_type() {
		return fnr_type;
	}

	public void setFnr_type(String fnr_type) {
		this.fnr_type = fnr_type;
	}
}
