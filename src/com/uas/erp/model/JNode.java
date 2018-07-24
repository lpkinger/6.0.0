package com.uas.erp.model;

import java.io.Serializable;

public class JNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jn_id;
	private String jn_name;
	private String jn_dealManId;
	private String jn_dealManName;
	private String jn_dealTime;
	private String jn_dealResult;
	private String jn_operatedDescription;
	private String jn_nodeDescription;
	private String jn_infoReceiver;
	private String jn_processInstanceId;
	private int jn_holdtime;
	private String jn_attachs;
	private String jn_attach;

	public String getJn_id() {
		return jn_id;
	}

	public void setJn_id(String jn_id) {
		this.jn_id = jn_id;
	}

	public String getJn_name() {
		return jn_name;
	}

	public void setJn_name(String jn_name) {
		this.jn_name = jn_name;
	}

	public String getJn_dealTime() {
		return jn_dealTime;
	}

	public void setJn_dealTime(String jn_dealTime) {
		this.jn_dealTime = jn_dealTime;
	}

	public String getJn_dealResult() {
		return jn_dealResult;
	}

	public void setJn_dealResult(String jn_dealResult) {
		this.jn_dealResult = jn_dealResult;
	}

	public String getJn_operatedDescription() {
		return jn_operatedDescription;
	}

	public void setJn_operatedDescription(String jn_operatedDescription) {
		this.jn_operatedDescription = jn_operatedDescription;
	}

	public String getJn_nodeDescription() {
		return jn_nodeDescription;
	}

	public void setJn_nodeDescription(String jn_nodeDescription) {
		this.jn_nodeDescription = jn_nodeDescription;
	}

	public String getJn_infoReceiver() {
		return jn_infoReceiver;
	}

	public void setJn_infoReceiver(String jn_infoReceiver) {
		this.jn_infoReceiver = jn_infoReceiver;
	}

	public String getJn_processInstanceId() {
		return jn_processInstanceId;
	}

	public void setJn_processInstanceId(String jn_processInstanceId) {
		this.jn_processInstanceId = jn_processInstanceId;
	}

	public String getJn_dealManId() {
		return jn_dealManId;
	}

	public void setJn_dealManId(String jn_dealManId) {
		this.jn_dealManId = jn_dealManId;
	}

	public String getJn_dealManName() {
		return jn_dealManName;
	}

	public void setJn_dealManName(String jn_dealManName) {
		this.jn_dealManName = jn_dealManName;
	}

	public int getJn_holdtime() {
		return jn_holdtime;
	}

	public void setJn_holdtime(int jn_holdtime) {
		this.jn_holdtime = jn_holdtime;
	}

	public String getJn_attachs() {
		return jn_attachs;
	}

	public void setJn_attachs(String jn_attachs) {
		this.jn_attachs = jn_attachs;
	}
	public String getJn_attach() {
		return jn_attach;
	}

	public void setJn_attach(String jn_attach) {
		this.jn_attach = jn_attach;
	}

}
