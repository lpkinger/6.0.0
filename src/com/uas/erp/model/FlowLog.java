package com.uas.erp.model;

/**
 * 流程日志
 * @author yingp
 * @date  2012-07-13 17:00:00
 */
import java.io.Serializable;
import java.util.Date;

public class FlowLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int fl_id;// ID
	private int fl_flowid;// 流程
	private String fl_flowname;// 流程名称
	private String fl_formurl;// 查看单据链接
	private String fl_ordercode;// 单据编号
	private int fl_orderid;// 单据ID
	private int fl_boxid;// 节点ID
	private String fl_boxname;// 节点名称
	private String fl_dotype;// 处理类型
	private String fl_domanid;// 处理人id
	private String fl_domanname;// 处理人名称
	private Date fl_dodate;// 处理日期
	private String fl_description;// 描述
	private String fl_enid;// 企业ID

	public int getFl_id() {
		return fl_id;
	}

	public void setFl_id(int fl_id) {
		this.fl_id = fl_id;
	}

	public int getFl_flowid() {
		return fl_flowid;
	}

	public void setFl_flowid(int fl_flowid) {
		this.fl_flowid = fl_flowid;
	}

	public String getFl_flowname() {
		return fl_flowname;
	}

	public void setFl_flowname(String fl_flowname) {
		this.fl_flowname = fl_flowname;
	}

	public String getFl_formurl() {
		return fl_formurl;
	}

	public void setFl_formurl(String fl_formurl) {
		this.fl_formurl = fl_formurl;
	}

	public String getFl_ordercode() {
		return fl_ordercode;
	}

	public void setFl_ordercode(String fl_ordercode) {
		this.fl_ordercode = fl_ordercode;
	}

	public int getFl_orderid() {
		return fl_orderid;
	}

	public void setFl_orderid(int fl_orderid) {
		this.fl_orderid = fl_orderid;
	}

	public int getFl_boxid() {
		return fl_boxid;
	}

	public void setFl_boxid(int fl_boxid) {
		this.fl_boxid = fl_boxid;
	}

	public String getFl_boxname() {
		return fl_boxname;
	}

	public void setFl_boxname(String fl_boxname) {
		this.fl_boxname = fl_boxname;
	}

	public String getFl_dotype() {
		return fl_dotype;
	}

	public void setFl_dotype(String fl_dotype) {
		this.fl_dotype = fl_dotype;
	}

	public String getFl_domanid() {
		return fl_domanid;
	}

	public void setFl_domanid(String fl_domanid) {
		this.fl_domanid = fl_domanid;
	}

	public String getFl_domanname() {
		return fl_domanname;
	}

	public void setFl_domanname(String fl_domanname) {
		this.fl_domanname = fl_domanname;
	}

	public Date getFl_dodate() {
		return fl_dodate;
	}

	public void setFl_dodate(Date fl_dodate) {
		this.fl_dodate = fl_dodate;
	}

	public String getFl_description() {
		return fl_description;
	}

	public void setFl_description(String fl_description) {
		this.fl_description = fl_description;
	}

	public String getFl_enid() {
		return fl_enid;
	}

	public void setFl_enid(String fl_enid) {
		this.fl_enid = fl_enid;
	}
}
