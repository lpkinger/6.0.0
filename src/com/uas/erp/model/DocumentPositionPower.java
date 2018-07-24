package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 岗位权限表
 * 
 * @author yingp
 * @date 2012-10-31 9:22:17
 */
public class DocumentPositionPower implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dpp_id;
	private int dpp_joid;// HrJobID
	private int dpp_dcpid;// DocumentPowerID
	private int dpp_add;// 新增文件夹
	private int dpp_download;// 下载文件
	private int dpp_upload;// 上传文件
	private int dpp_delete;// 删除
	private int dpp_update;// 修改

	public int getDpp_id() {
		return dpp_id;
	}

	public void setDpp_id(int dpp_id) {
		this.dpp_id = dpp_id;
	}

	public int getDpp_joid() {
		return dpp_joid;
	}

	public void setDpp_joid(int dpp_joid) {
		this.dpp_joid = dpp_joid;
	}

	public int getDpp_dcpid() {
		return dpp_dcpid;
	}

	public void setDpp_dcpid(int dpp_dcpid) {
		this.dpp_dcpid = dpp_dcpid;
	}

	public int getDpp_add() {
		return dpp_add;
	}

	public void setDpp_add(int dpp_add) {
		this.dpp_add = dpp_add;
	}

	public int getDpp_download() {
		return dpp_download;
	}

	public void setDpp_download(int dpp_download) {
		this.dpp_download = dpp_download;
	}

	public int getDpp_upload() {
		return dpp_upload;
	}

	public void setDpp_upload(int dpp_upload) {
		this.dpp_upload = dpp_upload;
	}

	public int getDpp_delete() {
		return dpp_delete;
	}

	public void setDpp_delete(int dpp_delete) {
		this.dpp_delete = dpp_delete;
	}

	public int getDpp_update() {
		return dpp_update;
	}

	public void setDpp_update(int dpp_update) {
		this.dpp_update = dpp_update;
	}

	@Override
	public String table() {
		return "PositionPower";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "pp_id" };
	}

}
