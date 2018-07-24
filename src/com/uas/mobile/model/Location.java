package com.uas.mobile.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.dao.Saveable;

/**
 * 位置信息对象，每条记录存储一个用户一个时刻的经纬度和位置
 * @author suntg
 * @date 2014年10月27日15:50:24
 *
 */
public class Location implements Saveable, Serializable{
	
	private static final long serialVersionUID = 9154546832037377352L;//序列编码
	private int lo_id;//ID
	private String lo_emcode;//员工编号
	private double lo_longitude;//经度
	private double lo_latitude;//纬度
	private Date lo_time;//时间
	private String lo_location;//位置名称
	public int getLo_id() {
		return lo_id;
	}
	public void setLo_id(int lo_id) {
		this.lo_id = lo_id;
	}
	public String getLo_emcode() {
		return lo_emcode;
	}
	public void setLo_emcode(String lo_emcode) {
		this.lo_emcode = lo_emcode;
	}
	public double getLo_longitude() {
		return lo_longitude;
	}
	public void setLo_longitude(double lo_longitude) {
		this.lo_longitude = lo_longitude;
	}
	public double getLo_latitude() {
		return lo_latitude;
	}
	public void setLo_latitude(double lo_latitude) {
		this.lo_latitude = lo_latitude;
	}
	public Date getLo_time() {
		return lo_time;
	}
	public void setLo_time(Date lo_time) {
		this.lo_time = lo_time;
	}
	public String getLo_location() {
		return lo_location;
	}
	public void setLo_location(String lo_location) {
		this.lo_location = lo_location;
	}
	
	@Override
	public String table() {
		return "location";
	}
	@Override
	public String[] keyColumns() {
		return new String[]{"lo_id"};
	}
	

}
