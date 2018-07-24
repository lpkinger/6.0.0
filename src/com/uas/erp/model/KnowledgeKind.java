package com.uas.erp.model;

import java.io.Serializable;

public class KnowledgeKind implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int kk_id;
	private String kk_kind;
	private int kk_kmid;
	private int personid;

	public int getKk_id() {
		return kk_id;
	}

	public void setKk_id(int kk_id) {
		this.kk_id = kk_id;
	}

	public String getKk_kind() {
		return kk_kind;
	}

	public void setKk_kind(String kk_kind) {
		this.kk_kind = kk_kind;
	}

	public int getKk_kmid() {
		return kk_kmid;
	}

	public void setKk_kmid(int kk_kmid) {
		this.kk_kmid = kk_kmid;
	}

	public int getPersonid() {
		return personid;
	}

	public void setPersonid(int personid) {
		this.personid = personid;
	}

}
