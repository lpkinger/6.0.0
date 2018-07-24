package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.dao.Saveable;

public class DocumentSetup implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ds_id;
	private String ds_table;
	private String ds_code;
	private String ds_name;
	private int ds_isautocode;
	private String ds_autocodecaller;
	private int ds_isautoaudit;
	private int ds_isautoprint;
	private String ds_dockind;
	private String ds_inorout;
	private int ds_toar;
	private int ds_toap;
	private int ds_ismulti;
	private List<DocumentHandler> documentHandlers;

	public int getDs_id() {
		return ds_id;
	}

	public void setDs_id(int ds_id) {
		this.ds_id = ds_id;
	}

	public String getDs_table() {
		return ds_table;
	}

	public void setDs_table(String ds_table) {
		this.ds_table = ds_table;
	}

	public String getDs_code() {
		return ds_code;
	}

	public void setDs_code(String ds_code) {
		this.ds_code = ds_code;
	}

	public String getDs_name() {
		return ds_name;
	}

	public void setDs_name(String ds_name) {
		this.ds_name = ds_name;
	}

	public int getDs_isautocode() {
		return ds_isautocode;
	}

	public void setDs_isautocode(int ds_isautocode) {
		this.ds_isautocode = ds_isautocode;
	}

	public String getDs_autocodecaller() {
		return ds_autocodecaller;
	}

	public void setDs_autocodecaller(String ds_autocodecaller) {
		this.ds_autocodecaller = ds_autocodecaller;
	}

	public int getDs_isautoaudit() {
		return ds_isautoaudit;
	}

	public void setDs_isautoaudit(int ds_isautoaudit) {
		this.ds_isautoaudit = ds_isautoaudit;
	}

	public int getDs_isautoprint() {
		return ds_isautoprint;
	}

	public void setDs_isautoprint(int ds_isautoprint) {
		this.ds_isautoprint = ds_isautoprint;
	}

	public String getDs_dockind() {
		return ds_dockind;
	}

	public void setDs_dockind(String ds_dockind) {
		this.ds_dockind = ds_dockind;
	}

	public String getDs_inorout() {
		return ds_inorout;
	}

	public void setDs_inorout(String ds_inorout) {
		this.ds_inorout = ds_inorout;
	}

	public int getDs_toar() {
		return ds_toar;
	}

	public void setDs_toar(int ds_toar) {
		this.ds_toar = ds_toar;
	}

	public int getDs_toap() {
		return ds_toap;
	}

	public void setDs_toap(int ds_toap) {
		this.ds_toap = ds_toap;
	}

	public int getDs_ismulti() {
		return ds_ismulti;
	}

	public void setDs_ismulti(int ds_ismulti) {
		this.ds_ismulti = ds_ismulti;
	}

	public List<DocumentHandler> getDocumentHandlers() {
		return documentHandlers;
	}

	public void setDocumentHandlers(List<DocumentHandler> documentHandlers) {
		this.documentHandlers = documentHandlers;
	}

	@Override
	public String table() {
		return "documentsetup";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ds_id" };
	}

}
