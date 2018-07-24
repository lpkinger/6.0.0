package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.SpObserver;

/**
 * 配置序列化导出
 * 
 * @author yingp
 *
 */
public class DumpFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String from;
	private String expDate;
	private String type;
	private String desc;
	private Object content;

	public DumpFile() {
		this.expDate = DateUtil.getCurrentDate();
		this.from = SpObserver.getSp();
	}

	public DumpFile(String type, String desc, Object content) {
		this();
		this.type = type;
		this.desc = desc;
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
