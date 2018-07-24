package com.uas.mobile.model;

import java.io.Serializable;


/**
 * @author :liuj 时间: 2015年1月13日 下午5:41:55
 * @注释:班次
 */
public class WorkDate implements  Serializable {

	private static final long serialVersionUID = 1L;
    
	private Integer wd_id;//班次ID
	private  String wd_code;//班次类型
	
	private String wd_ondutyone;//上班一
	private String wd_offdutyone;//下班一
	private String wd_latitude;//纬度坐标
	private String wd_longitude;//经度坐标
	private String wd_distanceforallow;//允许最大距离
	
	public Integer getWd_id() {
		return wd_id;
	}

	public void setWd_id(Integer wd_id) {
		this.wd_id = wd_id;
	}

	public String getWd_code() {
		return wd_code;
	}

	public void setWd_code(String wd_code) {
		this.wd_code = wd_code;
	}

	public String getWd_ondutyone() {
		return wd_ondutyone;
	}

	public void setWd_ondutyone(String wd_ondutyone) {
		this.wd_ondutyone = wd_ondutyone;
	}

	public String getWd_offdutyone() {
		return wd_offdutyone;
	}

	public void setWd_offdutyone(String wd_offdutyone) {
		this.wd_offdutyone = wd_offdutyone;
	}

	
	public String getWd_latitude() {
		return wd_latitude;
	}

	public void setWd_latitude(String wd_latitude) {
		this.wd_latitude = wd_latitude;
	}

	public String getWd_longitude() {
		return wd_longitude;
	}

	public void setWd_longitude(String wd_longitude) {
		this.wd_longitude = wd_longitude;
	}

	public String getWd_distanceforallow() {
		return wd_distanceforallow;
	}

	public void setWd_distanceforallow(String wd_distanceforallow) {
		this.wd_distanceforallow = wd_distanceforallow;
	}


}
