package com.uas.erp.model;

import java.io.Serializable;

/**
 * 职位设置
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class HRHeadShip implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int hs_id;// ID
	private String hs_name;// 职位名称
	private int hs_level;// 等级
	private String hs_grade;// 等级描述
	private float hs_rate;// 职位权重

	public int getHs_id() {
		return hs_id;
	}

	public void setHs_id(int hs_id) {
		this.hs_id = hs_id;
	}

	public String getHs_name() {
		return hs_name;
	}

	public void setHs_name(String hs_name) {
		this.hs_name = hs_name;
	}

	public int getHs_level() {
		return hs_level;
	}

	public void setHs_level(int hs_level) {
		this.hs_level = hs_level;
	}

	public String getHs_grade() {
		return hs_grade;
	}

	public void setHs_grade(String hs_grade) {
		this.hs_grade = hs_grade;
	}

	public float getHs_rate() {
		return hs_rate;
	}

	public void setHs_rate(float hs_rate) {
		this.hs_rate = hs_rate;
	}
}
