package com.uas.erp.model;

import java.io.Serializable;

public class DocumentCatalog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dc_Id;
	private String dc_DisplayName;
	private int dc_ParentId;
	private String dc_Url;
	private String dc_TabTitle;
	private String dc_isfile;
	private String dc_updatetime;// 修改日期
	private String dc_filesize;// 文件大小
	private String dc_version;// 文件版本
	private String dc_displayname_en;// English
	private String dc_displayname_tw;// 繁體
	private String dc_deleteable;// 是否允许删除
	private int dc_creator_id;
	private String dc_creator;

	public String toString() {
		return "id:" + dc_Id + "  parentId:" + dc_ParentId + "  text:" + dc_DisplayName;
	}

	public int getDc_Id() {
		return dc_Id;
	}

	public void setDc_Id(int dc_Id) {
		this.dc_Id = dc_Id;
	}

	public String getDc_DisplayName() {
		return dc_DisplayName;
	}

	public void setDc_DisplayName(String dc_DisplayName) {
		this.dc_DisplayName = dc_DisplayName;
	}

	public int getDc_ParentId() {
		return dc_ParentId;
	}

	public void setDc_ParentId(int dc_ParentId) {
		this.dc_ParentId = dc_ParentId;
	}

	public String getDc_Url() {
		return dc_Url;
	}

	public void setDc_Url(String dc_Url) {
		this.dc_Url = dc_Url;
	}

	public String getDc_TabTitle() {
		return dc_TabTitle;
	}

	public void setDc_TabTitle(String dc_TabTitle) {
		this.dc_TabTitle = dc_TabTitle;
	}

	public String getDc_isfile() {
		return dc_isfile;
	}

	public void setDc_isfile(String dc_isfile) {
		this.dc_isfile = dc_isfile;
	}

	public String getDc_displayname_en() {
		return dc_displayname_en;
	}

	public void setDc_displayname_en(String dc_displayname_en) {
		this.dc_displayname_en = dc_displayname_en;
	}

	public String getDc_displayname_tw() {
		return dc_displayname_tw;
	}

	public void setDc_displayname_tw(String dc_displayname_tw) {
		this.dc_displayname_tw = dc_displayname_tw;
	}

	public String getDc_deleteable() {
		return dc_deleteable;
	}

	public void setDc_deleteable(String dc_deleteable) {
		this.dc_deleteable = dc_deleteable;
	}

	// public Date getDc_date() {
	// return dc_date;
	// }
	// public void setDc_date(Date dc_date) {
	// this.dc_date = dc_date;
	// }
	public String getDc_filesize() {
		return dc_filesize;
	}

	public void setDc_filesize(String dc_filesize) {
		this.dc_filesize = dc_filesize;
	}

	public String getDc_version() {
		return dc_version;
	}

	public void setDc_version(String dc_version) {
		this.dc_version = dc_version;
	}

	public String getDc_updatetime() {
		return dc_updatetime;
	}

	public void setDc_updatetime(String dc_updatetime) {
		this.dc_updatetime = dc_updatetime;
	}

	public String getDc_creator() {
		return dc_creator;
	}

	public int getDc_creator_id() {
		return dc_creator_id;
	}

	public void setDc_creator(String dc_creator) {
		this.dc_creator = dc_creator;
	}

	public void setDc_creator_id(int dc_creator_id) {
		this.dc_creator_id = dc_creator_id;
	}

}
