package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 工作台
 */
public class WorkBench implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int wb_id;
	private String wb_name;
	private int wb_emid;
	private int wb_isuse;
	private int wb_detno;
	private double wb_width;
	private double wb_height;

	public int getWb_id() {
		return wb_id;
	}

	public void setWb_id(int wb_id) {
		this.wb_id = wb_id;
	}

	public String getWb_name() {
		return wb_name;
	}

	public void setWb_name(String wb_name) {
		this.wb_name = wb_name;
	}

	public int getWb_emid() {
		return wb_emid;
	}

	public void setWb_emid(int wb_emid) {
		this.wb_emid = wb_emid;
	}

	public int getWb_isuse() {
		return wb_isuse;
	}

	public void setWb_isuse(int wb_isuse) {
		this.wb_isuse = wb_isuse;
	}

	public int getWb_detno() {
		return wb_detno;
	}

	public void setWb_detno(int wb_detno) {
		this.wb_detno = wb_detno;
	}

	public double getWb_width() {
		return wb_width;
	}

	public void setWb_width(double wb_width) {
		this.wb_width = wb_width;
	}

	public double getWb_height() {
		return wb_height;
	}

	public void setWb_height(double wb_height) {
		this.wb_height = wb_height;
	}

	@Override
	public String table() {
		return "WorkBench";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "wb_id" };
	}

	public WorkBench() {

	}

	/**
	 * @param wb_name
	 *            模块名称
	 * @param wb_emid
	 *            员工ID
	 * @param wb_isuse
	 *            是否显示
	 * @param wb_detno
	 *            排序
	 * @param wb_width
	 *            宽度%
	 * @param wb_height
	 *            高度%
	 */
	public WorkBench(String wb_name, int wb_emid, int wb_isuse, int wb_detno, double wb_width, double wb_height) {
		this.wb_detno = wb_detno;
		this.wb_emid = wb_emid;
		this.wb_height = wb_height;
		this.wb_isuse = wb_isuse;
		this.wb_width = wb_width;
		this.wb_name = wb_name;
	}
}
