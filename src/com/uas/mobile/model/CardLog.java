package com.uas.mobile.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author :liuj 时间: 2015年1月14日 下午7:32:17
 * @注释: 签到记录表
 */
public class CardLog  implements  Serializable  {
    
	
	private Integer cl_id;
    private String cl_cardcode;
    private Integer cl_emid;
    
    private Date cl_time;
    private String cl_status;
    private String cl_emcode;
    private String cl_latitude;
    private String cl_longitude;
    private Integer cl_isproxy;
    private int cl_imageforproxy;
    
    
    
	
	public Integer getCl_id() {
		return cl_id;
	}

	public void setCl_id(Integer cl_id) {
		this.cl_id = cl_id;
	}

	public String getCl_cardcode() {
		return cl_cardcode;
	}

	public void setCl_cardcode(String cl_cardcode) {
		this.cl_cardcode = cl_cardcode;
	}

	public Integer getCl_emid() {
		return cl_emid;
	}

	public void setCl_emid(Integer cl_emid) {
		this.cl_emid = cl_emid;
	}


	public Date getCl_time() {
		return cl_time;
	}

	public void setCl_time(Date cl_time) {
		this.cl_time = cl_time;
	}

	public String getCl_status() {
		return cl_status;
	}

	public void setCl_status(String cl_status) {
		this.cl_status = cl_status;
	}

	public String getCl_emcode() {
		return cl_emcode;
	}

	public void setCl_emcode(String cl_emcode) {
		this.cl_emcode = cl_emcode;
	}

	public String getCl_latitude() {
		return cl_latitude;
	}

	public void setCl_latitude(String cl_latitude) {
		this.cl_latitude = cl_latitude;
	}

	public String getCl_longitude() {
		return cl_longitude;
	}

	public void setCl_longitude(String cl_longitude) {
		this.cl_longitude = cl_longitude;
	}

	public Integer getCl_isproxy() {
		return cl_isproxy;
	}

	public void setCl_isproxy(Integer cl_isproxy) {
		this.cl_isproxy = cl_isproxy;
	}

	public int getCl_imageforproxy() {
		return cl_imageforproxy;
	}

	public void setCl_imageforproxy(int cl_imageforproxy) {
		this.cl_imageforproxy = cl_imageforproxy;
	}


}
