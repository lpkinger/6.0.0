package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

import com.uas.erp.dao.Saveable;

/**
 * UU聊天消息接口<br>
 * 
 * @author yingp
 */
public class Notification implements Serializable, Saveable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1802462562649480413L;

	private int no_id;
	private int no_fromenuu;
	private int no_fromemuu;
	private int no_toenuu;
	private int no_toemuu;
	private String no_fromenname;
	private String no_fromemname;
	private String no_toenname;
	private String no_toemname;
	private Date no_date;
	private int no_type = ALIGN_RIGHT;// 弹出类型
	private String no_orderid;// 单号
	private String no_remark;// 备注
	private int no_status = STATUS_UNTREATED;
	private int no_ordertype = TYPE_CHAT;// 订单类型
	private String no_address;// 消息对应的链接地址
	private String no_subject;// 标题
	private String no_body;// 正文
	private int no_important = IMPORTANT;// 消息的重要性
	private int no_systype = SYS_ERP;// 消息的来源

	// {no_status}
	public static final int STATUS_UNTREATED = -1;// -1表示UU系统收到还未处理的消息
	public static final int STATUS_READY = 0;// 0表示准备处理
	public static final int STATUS_HANDLE = 1;// 1表示已经处理完成

	// {no_type}消息类型，用来控制客户端的显示效果
	public static final int ALIGN_RIGHT = 1;// 右下角弹窗
	public static final int ALIGN_HORN = 2;// 小喇叭提醒
	public static final int ALIGN_CENTER = 3;// 面板居中弹窗

	// {no_ordertype}订单类型
	public static final int TYPE_CHAT = 0;// 0~99表示聊天信息，点击将打开聊天对话窗口
	public static final int TYPE_ORDER = 100;// 100~599表示订单信息，点击打开链接
	public static final int TYPE_PANEL = 600;// 600~799表示用面板显示详细信息，点击将打开面板显示该条消息的详细信息

	// {no_important}消息的重要性
	public static final int NORMAL = 0;// 0表示一般，弹窗提醒后将在一段时间内消息
	public static final int IMPORTANT = 1;// 1表示重要，弹窗提醒后用户不做处理将一直显示
	public static final int VERY_IMPORTANT = 2;// 2表示非常重要，如果有多条提醒消息，非常重要的消息将优先显示，始终置顶

	// {no_systype}消息的来源
	public static final int SYS_UU = 0;// 0表示UU系统消息
	public static final int SYS_B2B = 1;// 1表示B2B系统
	public static final int SYS_ERP = 2;// 2表示ERP系统

	public int getNo_id() {
		return no_id;
	}

	public void setNo_id(int no_id) {
		this.no_id = no_id;
	}

	public int getNo_fromenuu() {
		return no_fromenuu;
	}

	public void setNo_fromenuu(int no_fromenuu) {
		this.no_fromenuu = no_fromenuu;
	}

	public int getNo_fromemuu() {
		return no_fromemuu;
	}

	public void setNo_fromemuu(int no_fromemuu) {
		this.no_fromemuu = no_fromemuu;
	}

	public int getNo_toenuu() {
		return no_toenuu;
	}

	public void setNo_toenuu(int no_toenuu) {
		this.no_toenuu = no_toenuu;
	}

	public int getNo_toemuu() {
		return no_toemuu;
	}

	public void setNo_toemuu(int no_toemuu) {
		this.no_toemuu = no_toemuu;
	}

	public String getNo_fromenname() {
		return no_fromenname;
	}

	public void setNo_fromenname(String no_fromenname) {
		this.no_fromenname = no_fromenname;
	}

	public String getNo_fromemname() {
		return no_fromemname;
	}

	public void setNo_fromemname(String no_fromemname) {
		this.no_fromemname = no_fromemname;
	}

	public String getNo_toenname() {
		return no_toenname;
	}

	public void setNo_toenname(String no_toenname) {
		this.no_toenname = no_toenname;
	}

	public String getNo_toemname() {
		return no_toemname;
	}

	public void setNo_toemname(String no_toemname) {
		this.no_toemname = no_toemname;
	}

	public Date getNo_date() {
		return no_date;
	}

	public void setNo_date(Date no_date) {
		this.no_date = no_date;
	}

	public int getNo_type() {
		return no_type;
	}

	public void setNo_type(int no_type) {
		this.no_type = no_type;
	}

	public String getNo_orderid() {
		return no_orderid;
	}

	public void setNo_orderid(String no_orderid) {
		this.no_orderid = no_orderid;
	}

	public String getNo_remark() {
		return no_remark;
	}

	public void setNo_remark(String no_remark) {
		this.no_remark = no_remark;
	}

	public int getNo_status() {
		return no_status;
	}

	public void setNo_status(int no_status) {
		this.no_status = no_status;
	}

	public int getNo_ordertype() {
		return no_ordertype;
	}

	public void setNo_ordertype(int no_ordertype) {
		this.no_ordertype = no_ordertype;
	}

	public String getNo_address() {
		return no_address;
	}

	public void setNo_address(String no_address) {
		this.no_address = no_address;
	}

	public String getNo_subject() {
		return no_subject;
	}

	public void setNo_subject(String no_subject) {
		this.no_subject = no_subject;
	}

	public String getNo_body() {
		return no_body;
	}

	public void setNo_body(String no_body) {
		this.no_body = no_body;
	}

	public int getNo_important() {
		return no_important;
	}

	public void setNo_important(int no_important) {
		this.no_important = no_important;
	}

	public int getNo_systype() {
		return no_systype;
	}

	public void setNo_systype(int no_systype) {
		this.no_systype = no_systype;
	}

	@Override
	public String table() {
		return "Notification";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "no_id" };
	}

}