package com.uas.erp.model;

import java.io.Serializable;

public class CustomFlowDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int cfd_id;
	private int cfd_cfid;
	private String cfd_code;
	// /private String cfd_source;
	// /private int cfd_sourceId;
	private int cfd_detno;
	private String cfd_actorUsers;
	private String cfd_notifier;
	private String cfd_description;

	public String getCfd_code() {
		return cfd_code;
	}

	public void setCfd_code(String cfd_code) {
		this.cfd_code = cfd_code;
	}

	public int getCfd_id() {
		return cfd_id;
	}

	public void setCfd_id(int cfd_id) {
		this.cfd_id = cfd_id;
	}

	public int getCfd_cfid() {
		return cfd_cfid;
	}

	public void setCfd_cfid(int cfd_cfid) {
		this.cfd_cfid = cfd_cfid;
	}

	public int getCfd_detno() {
		return cfd_detno;
	}

	public void setCfd_detno(int cfd_detno) {
		this.cfd_detno = cfd_detno;
	}

	public String getCfd_actorUsers() {
		return cfd_actorUsers;
	}

	public void setCfd_actorUsers(String cfd_actorUsers) {
		this.cfd_actorUsers = cfd_actorUsers;
	}

	public String getCfd_notifier() {
		return cfd_notifier;
	}

	public void setCfd_notifier(String cfd_notifier) {
		this.cfd_notifier = cfd_notifier;
	}

	public String getCfd_description() {
		return cfd_description;
	}

	public void setCfd_description(String cfd_description) {
		this.cfd_description = cfd_description;
	}

}
