package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 需审批节点
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class FlowBox implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fb_id;// ID
	private int fb_flowid;// 实例流程ID
	private int fb_nodeid;// 节点ID
	private String fb_nodename;// 节点名称
	private int fb_level;// 层级
	private String fb_status;// 状态(no,yes,pass,finish)
	private Date fb_begindate;// 触发日期
	private Date fb_effectdate;// 最近动作日期
	private Date fb_enddate;// 结束日期
	private Date fb_deaddate;// 过期日期
	private String fb_headman;// 责任人
	private String fb_didman;// 处理人

	public int getFb_id() {
		return fb_id;
	}

	public void setFb_id(int fb_id) {
		this.fb_id = fb_id;
	}

	public int getFb_flowid() {
		return fb_flowid;
	}

	public void setFb_flowid(int fb_flowid) {
		this.fb_flowid = fb_flowid;
	}

	public int getFb_nodeid() {
		return fb_nodeid;
	}

	public void setFb_nodeid(int fb_nodeid) {
		this.fb_nodeid = fb_nodeid;
	}

	public String getFb_nodename() {
		return fb_nodename;
	}

	public void setFb_nodename(String fb_nodename) {
		this.fb_nodename = fb_nodename;
	}

	public int getFb_level() {
		return fb_level;
	}

	public void setFb_level(int fb_level) {
		this.fb_level = fb_level;
	}

	public String getFb_status() {
		return fb_status;
	}

	public void setFb_status(String fb_status) {
		this.fb_status = fb_status;
	}

	public Date getFb_begindate() {
		return fb_begindate;
	}

	public void setFb_begindate(Date fb_begindate) {
		this.fb_begindate = fb_begindate;
	}

	public Date getFb_effectdate() {
		return fb_effectdate;
	}

	public void setFb_effectdate(Date fb_effectdate) {
		this.fb_effectdate = fb_effectdate;
	}

	public Date getFb_enddate() {
		return fb_enddate;
	}

	public void setFb_enddate(Date fb_enddate) {
		this.fb_enddate = fb_enddate;
	}

	public Date getFb_deaddate() {
		return fb_deaddate;
	}

	public void setFb_deaddate(Date fb_deaddate) {
		this.fb_deaddate = fb_deaddate;
	}

	public String getFb_headman() {
		return fb_headman;
	}

	public void setFb_headman(String fb_headman) {
		this.fb_headman = fb_headman;
	}

	public String getFb_didman() {
		return fb_didman;
	}

	public void setFb_didman(String fb_didman) {
		this.fb_didman = fb_didman;
	}
}
