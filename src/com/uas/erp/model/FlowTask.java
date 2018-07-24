package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 单据流程
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ft_id;// ID
	private String ft_caller;// FlowCaller->fc_caller, 对应Form.fo_caller
	private String ft_ordercode;// 单据编号
	private int ft_orderid;// 单据主键
	private int ft_fiid;// 引用流程ID
	private String ft_finame;// 流程名称
	private String ft_status;// 流转状态
	private String ft_title;// 单据来源
	private String ft_formurl;// 单据查看URL
	private String ft_startman;// 发起人
	private String ft_stopman;// 结束人
	private Date ft_begindate;// 发起时间
	private Date ft_enddate;// 结束时间
	private Date ft_effectdate;// 最近动作时间
	private int ft_nownodeid;// 当前节点id
	private String ft_nownodename;// 当前节点名称
	private String ft_nowman;// 当前处理人名称
	private int ft_enid;// 企业ID

	public int getFt_id() {
		return ft_id;
	}

	public void setFt_id(int ft_id) {
		this.ft_id = ft_id;
	}

	public String getFt_caller() {
		return ft_caller;
	}

	public void setFt_caller(String ft_caller) {
		this.ft_caller = ft_caller;
	}

	public String getFt_ordercode() {
		return ft_ordercode;
	}

	public void setFt_ordercode(String ft_ordercode) {
		this.ft_ordercode = ft_ordercode;
	}

	public int getFt_orderid() {
		return ft_orderid;
	}

	public void setFt_orderid(int ft_orderid) {
		this.ft_orderid = ft_orderid;
	}

	public int getFt_fiid() {
		return ft_fiid;
	}

	public void setFt_fiid(int ft_fiid) {
		this.ft_fiid = ft_fiid;
	}

	public String getFt_finame() {
		return ft_finame;
	}

	public void setFt_finame(String ft_finame) {
		this.ft_finame = ft_finame;
	}

	public String getFt_status() {
		return ft_status;
	}

	public void setFt_status(String ft_status) {
		this.ft_status = ft_status;
	}

	public String getFt_title() {
		return ft_title;
	}

	public void setFt_title(String ft_title) {
		this.ft_title = ft_title;
	}

	public String getFt_formurl() {
		return ft_formurl;
	}

	public void setFt_formurl(String ft_formurl) {
		this.ft_formurl = ft_formurl;
	}

	public String getFt_startman() {
		return ft_startman;
	}

	public void setFt_startman(String ft_startman) {
		this.ft_startman = ft_startman;
	}

	public String getFt_stopman() {
		return ft_stopman;
	}

	public void setFt_stopman(String ft_stopman) {
		this.ft_stopman = ft_stopman;
	}

	public Date getFt_begindate() {
		return ft_begindate;
	}

	public void setFt_begindate(Date ft_begindate) {
		this.ft_begindate = ft_begindate;
	}

	public Date getFt_enddate() {
		return ft_enddate;
	}

	public void setFt_enddate(Date ft_enddate) {
		this.ft_enddate = ft_enddate;
	}

	public Date getFt_effectdate() {
		return ft_effectdate;
	}

	public void setFt_effectdate(Date ft_effectdate) {
		this.ft_effectdate = ft_effectdate;
	}

	public int getFt_nownodeid() {
		return ft_nownodeid;
	}

	public void setFt_nownodeid(int ft_nownodeid) {
		this.ft_nownodeid = ft_nownodeid;
	}

	public String getFt_nownodename() {
		return ft_nownodename;
	}

	public void setFt_nownodename(String ft_nownodename) {
		this.ft_nownodename = ft_nownodename;
	}

	public String getFt_nowman() {
		return ft_nowman;
	}

	public void setFt_nowman(String ft_nowman) {
		this.ft_nowman = ft_nowman;
	}

	public int getFt_enid() {
		return ft_enid;
	}

	public void setFt_enid(int ft_enid) {
		this.ft_enid = ft_enid;
	}
}
