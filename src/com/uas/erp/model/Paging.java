package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.uas.erp.dao.Saveable;

public class Paging implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pr_id;
	private String pr_releaser;
	private int pr_releaserid;
	private Date pr_date;
	private String pr_recipientid;
	private String pr_recipient;
	private String pr_mobile;
	private int pr_status;
	private String pr_attach;
	private String pr_context;
	private List<String> files = new ArrayList<String>();
	private boolean flag;// 是否已读
	private boolean reply;// 是否需要回复
	private boolean attch;// 是否有附件

	@Override
	public String table() {
		return "Paging";
	}

	public int getPr_id() {
		return pr_id;
	}

	public void setPr_id(int pr_id) {
		this.pr_id = pr_id;
	}

	public String getPr_releaser() {
		return pr_releaser;
	}

	public void setPr_releaser(String pr_releaser) {
		this.pr_releaser = pr_releaser;
	}

	public int getPr_releaserid() {
		return pr_releaserid;
	}

	public void setPr_releaserid(int pr_releaserid) {
		this.pr_releaserid = pr_releaserid;
	}

	public Date getPr_date() {
		return pr_date;
	}

	public void setPr_date(Date pr_date) {
		this.pr_date = pr_date;
	}

	public String getPr_recipientid() {
		return pr_recipientid;
	}

	public void setPr_recipientid(String pr_recipientid) {
		this.pr_recipientid = pr_recipientid;
	}

	public String getPr_recipient() {
		return pr_recipient;
	}

	public void setPr_recipient(String pr_recipient) {
		this.pr_recipient = pr_recipient;
	}

	public String getPr_mobile() {
		return pr_mobile;
	}

	public void setPr_mobile(String pr_mobile) {
		this.pr_mobile = pr_mobile;
	}

	public int getPr_status() {
		return pr_status;
	}

	public void setPr_status(int pr_status) {
		this.pr_status = pr_status;
	}

	public String getPr_attach() {
		return pr_attach;
	}

	public void setPr_attach(String pr_attach) {
		this.pr_attach = pr_attach;
	}

	public String getPr_context() {
		return pr_context;
	}

	public void setPr_context(String pr_context) {
		this.pr_context = pr_context;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
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

	@Override
	public String[] keyColumns() {
		return new String[] { "pr_id" };
	}

}
