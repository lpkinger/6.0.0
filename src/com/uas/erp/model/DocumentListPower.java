package com.uas.erp.model;

import java.io.Serializable;

public class DocumentListPower implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dlp_id;
	private int dlp_see;
	private int dlp_delete;
	private int dlp_share;
	private int dlp_edit;
	private int dlp_journal;
	private int dlp_download;
	private int dlp_joid;
	private int dlp_dclid;

	public int getDlp_id() {
		return dlp_id;
	}

	public void setDlp_id(int dlp_id) {
		this.dlp_id = dlp_id;
	}

	public int getDlp_see() {
		return dlp_see;
	}

	public void setDlp_see(int dlp_see) {
		this.dlp_see = dlp_see;
	}

	public int getDlp_delete() {
		return dlp_delete;
	}

	public void setDlp_delete(int dlp_delete) {
		this.dlp_delete = dlp_delete;
	}

	public int getDlp_share() {
		return dlp_share;
	}

	public void setDlp_share(int dlp_share) {
		this.dlp_share = dlp_share;
	}

	public int getDlp_edit() {
		return dlp_edit;
	}

	public void setDlp_edit(int dlp_edit) {
		this.dlp_edit = dlp_edit;
	}

	public int getDlp_journal() {
		return dlp_journal;
	}

	public void setDlp_journal(int dlp_journal) {
		this.dlp_journal = dlp_journal;
	}

	public int getDlp_download() {
		return dlp_download;
	}

	public void setDlp_download(int dlp_download) {
		this.dlp_download = dlp_download;
	}

	public int getDlp_joid() {
		return dlp_joid;
	}

	public void setDlp_joid(int dlp_joid) {
		this.dlp_joid = dlp_joid;
	}

	public int getDlp_dclid() {
		return dlp_dclid;
	}

	public void setDlp_dlid(int dlp_dclid) {
		this.dlp_dclid = dlp_dclid;
	}

}
