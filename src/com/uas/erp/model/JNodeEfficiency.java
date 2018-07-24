package com.uas.erp.model;

import java.io.Serializable;

public class JNodeEfficiency implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int je_id;
	private String je_processInstanceName;
	private String je_processInstanceId;
	private String je_nodeName;
	private String je_nodeDealMan;
	private long je_nodeMinutes;
	private long je_wholeMinutes;
	private long je_standardMinutes;
	private long je_nodeTimeout;

	public int getJe_id() {
		return je_id;
	}

	public void setJe_id(int je_id) {
		this.je_id = je_id;
	}

	public String getJe_processInstanceName() {
		return je_processInstanceName;
	}

	public void setJe_processInstanceName(String je_processInstanceName) {
		this.je_processInstanceName = je_processInstanceName;
	}

	public String getJe_nodeName() {
		return je_nodeName;
	}

	public void setJe_nodeName(String je_nodeName) {
		this.je_nodeName = je_nodeName;
	}

	public String getJe_nodeDealMan() {
		return je_nodeDealMan;
	}

	public void setJe_nodeDealMan(String je_nodeDealMan) {
		this.je_nodeDealMan = je_nodeDealMan;
	}

	public long getJe_nodeMinutes() {
		return je_nodeMinutes;
	}

	public void setJe_nodeMinutes(long je_nodeMinutes) {
		this.je_nodeMinutes = je_nodeMinutes;
	}

	public long getJe_wholeMinutes() {
		return je_wholeMinutes;
	}

	public void setJe_wholeMinutes(long je_wholeMinutes) {
		this.je_wholeMinutes = je_wholeMinutes;
	}

	public long getJe_standardMinutes() {
		return je_standardMinutes;
	}

	public void setJe_standardMinutes(long je_standardMinutes) {
		this.je_standardMinutes = je_standardMinutes;
	}

	public long getJe_nodeTimeout() {
		return je_nodeTimeout;
	}

	public void setJe_nodeTimeout(long je_nodeTimeout) {
		this.je_nodeTimeout = je_nodeTimeout;
	}

	public String getJe_processInstanceId() {
		return je_processInstanceId;
	}

	public void setJe_processInstanceId(String je_processInstanceId) {
		this.je_processInstanceId = je_processInstanceId;
	}

	public JNodeEfficiency() {
	}

	public JNodeEfficiency(String je_processInstanceName, String je_processInstanceId, String je_nodeName, String je_nodeDealMan,
			long je_nodeMinutes, long je_wholeMinutes, long je_standardMinutes, long je_nodeTimeout) {

		this.je_processInstanceName = je_processInstanceName;
		this.je_processInstanceId = je_processInstanceId;
		this.je_nodeName = je_nodeName;
		this.je_nodeDealMan = je_nodeDealMan;
		this.je_nodeMinutes = je_nodeMinutes;
		this.je_wholeMinutes = je_wholeMinutes;
		this.je_standardMinutes = je_standardMinutes;
		this.je_nodeTimeout = je_nodeTimeout;
	}
}
