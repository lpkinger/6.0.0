package com.uas.erp.model;

import java.io.Serializable;

public class JnodeRelation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int jr_id;
	private String jr_processdefid;
	private String jr_name;
	private String jr_from;
	private String jr_to;
	private String jr_condition;
	private String jr_nodedealman;
	private String jr_nodedealmanname;
	private String jr_type;
	private String jr_canextra;

	public int getJr_id() {
		return jr_id;
	}

	public void setJr_id(int jr_id) {
		this.jr_id = jr_id;
	}

	public String getJr_processdefid() {
		return jr_processdefid;
	}

	public void setJr_processdefid(String jr_processdefid) {
		this.jr_processdefid = jr_processdefid;
	}

	public String getJr_name() {
		return jr_name;
	}

	public void setJr_name(String jr_name) {
		this.jr_name = jr_name;
	}

	public String getJr_from() {
		return jr_from;
	}

	public void setJr_from(String jr_from) {
		this.jr_from = jr_from;
	}

	public String getJr_to() {
		return jr_to;
	}

	public void setJr_to(String jr_to) {
		this.jr_to = jr_to;
	}

	public String getJr_condition() {
		return jr_condition;
	}

	public void setJr_condition(String jr_condition) {
		this.jr_condition = jr_condition;
	}

	public String getJr_nodedealman() {
		return jr_nodedealman;
	}

	public void setJr_nodedealman(String jr_nodedealman) {
		this.jr_nodedealman = jr_nodedealman;
	}

	public String getJr_nodedealmanname() {
		return jr_nodedealmanname;
	}

	public void setJr_nodedealmanname(String jr_nodedealmanname) {
		this.jr_nodedealmanname = jr_nodedealmanname;
	}

	public String getJr_type() {
		return jr_type;
	}

	public void setJr_type(String jr_type) {
		this.jr_type = jr_type;
	}

	public String getJr_canextra() {
		return jr_canextra;
	}

	public void setJr_canextra(String jr_canextra) {
		this.jr_canextra = jr_canextra;
	}

}
