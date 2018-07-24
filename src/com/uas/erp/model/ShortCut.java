package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 快捷栏
 */
public class ShortCut implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sc_id;
	private int sc_emid;
	private String sc_name;
	private int sc_isuse;
	private int sc_detno;

	public int getSc_id() {
		return sc_id;
	}

	public void setSc_id(int sc_id) {
		this.sc_id = sc_id;
	}

	public int getSc_emid() {
		return sc_emid;
	}

	public void setSc_emid(int sc_emid) {
		this.sc_emid = sc_emid;
	}

	public String getSc_name() {
		return sc_name;
	}

	public void setSc_name(String sc_name) {
		this.sc_name = sc_name;
	}

	public int getSc_isuse() {
		return sc_isuse;
	}

	public void setSc_isuse(int sc_isuse) {
		this.sc_isuse = sc_isuse;
	}

	public int getSc_detno() {
		return sc_detno;
	}

	public void setSc_detno(int sc_detno) {
		this.sc_detno = sc_detno;
	}

	public ShortCut() {

	}

	/**
	 * @param sc_emid
	 *            员工ID
	 * @param sc_name
	 *            描述名
	 * @param sc_isuse
	 *            是否(1/0)使用
	 * @param sc_detno
	 *            排列顺序
	 */
	public ShortCut(int sc_emid, String sc_name, int sc_isuse, int sc_detno) {
		this.sc_detno = sc_detno;
		this.sc_emid = sc_emid;
		this.sc_isuse = sc_isuse;
		this.sc_name = sc_name;
	}

	@Override
	public String table() {
		return "ShortCut";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "sc_id" };
	}
}
