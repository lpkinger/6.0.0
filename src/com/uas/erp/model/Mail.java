package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.uas.erp.dao.Saveable;

public class Mail implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ma_id;
	private String ma_uid;
	private String ma_from;
	private String ma_subject;
	private String ma_senddate;
	private String ma_receaddr;
	private String ma_context;
	private List<String> files = new ArrayList<String>();
	private String ma_attach;
	private int ma_status;
	private boolean flag;// 是否已读
	private boolean reply;// 是否需要回复
	private boolean attch;// 是否有附件
	private String group;// 分组

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMa_uid() {
		return ma_uid;
	}

	public void setMa_uid(String ma_uid) {
		this.ma_uid = ma_uid;
	}

	public boolean isReply() {
		return reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public boolean isAttch() {
		return attch;
	}

	public void setAttch(boolean attch) {
		this.attch = attch;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getMa_id() {
		return ma_id;
	}

	public void setMa_id(int ma_id) {
		this.ma_id = ma_id;
	}

	public String getMa_from() {
		return ma_from;
	}

	public void setMa_from(String ma_from) {
		this.ma_from = ma_from;
	}

	public String getMa_context() {
		return ma_context;
	}

	public void setMa_context(String ma_context) {
		this.ma_context = ma_context;
	}

	public String getMa_subject() {
		return ma_subject;
	}

	public void setMa_subject(String ma_subject) {
		this.ma_subject = ma_subject;
	}

	public String getMa_senddate() {
		return ma_senddate;
	}

	public void setMa_senddate(String ma_senddate) {
		this.ma_senddate = ma_senddate;
	}

	public String getMa_receaddr() {
		return ma_receaddr;
	}

	public void setMa_receaddr(String ma_receaddr) {
		this.ma_receaddr = ma_receaddr;
	}

	public String getMa_attach() {
		return ma_attach;
	}

	public void setMa_attach(String ma_attach) {
		this.ma_attach = ma_attach;
	}

	public int getMa_status() {
		return ma_status;
	}

	public void setMa_status(int ma_status) {
		this.ma_status = ma_status;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	@Override
	public String table() {
		return "Mail";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ma_id" };
	}

}
