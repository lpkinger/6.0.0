package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.logging.BufferedLogable;
import com.uas.erp.dao.Saveable;

/**
 * debug日志
 * 
 * @author yingp
 *
 */
public class DebugLog extends BufferedLogable implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String dl_id;
	private String dl_man;
	private String dl_url;
	private int dl_step;
	private Long dl_time;
	private Long dl_date;
	private String dl_master;

	public String getDl_id() {
		return dl_id;
	}

	public void setDl_id(String dl_id) {
		this.dl_id = dl_id;
	}

	public String getDl_man() {
		return dl_man;
	}

	public void setDl_man(String dl_man) {
		this.dl_man = dl_man;
	}

	public String getDl_url() {
		return dl_url;
	}

	public void setDl_url(String dl_url) {
		this.dl_url = dl_url;
	}

	public int getDl_step() {
		return dl_step;
	}

	public void setDl_step(int dl_step) {
		this.dl_step = dl_step;
	}

	public Long getDl_time() {
		return dl_time;
	}

	public void setDl_time(Long dl_time) {
		this.dl_time = dl_time;
	}

	public Long getDl_date() {
		return dl_date;
	}

	public void setDl_date(Long dl_date) {
		this.dl_date = dl_date;
	}

	public String getDl_master() {
		return dl_master;
	}

	public void setDl_master(String dl_master) {
		this.dl_master = dl_master;
	}

	@Override
	public String bufferedMessage() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.dl_id).append(separator);
		sb.append(this.dl_man).append(separator);
		sb.append(this.dl_url).append(separator);
		sb.append(this.dl_step).append(separator);
		sb.append(this.dl_time).append(separator);
		sb.append(this.dl_date).append(separator);
		sb.append(this.dl_master);
		return sb.toString();
	}

	@Override
	public void bufferedLog(String bufferedMessage) {
		String[] strArray = bufferedMessage.split(separator);
		if (strArray.length >= 7) {
			this.dl_id = strArray[0];
			this.dl_man = strArray[1];
			this.dl_url = strArray[2];
			this.dl_step = Integer.parseInt(strArray[3]);
			this.dl_time = Long.parseLong(strArray[4]);
			this.dl_date = Long.parseLong(strArray[5]);
			this.dl_master = strArray[6];
		}
	}

	@Override
	public String table() {
		return "DEBUGLOG";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

	public DebugLog() {
	}

	/**
	 * @param url
	 *            请求uri
	 * @param man
	 *            操作人
	 * @param master
	 *            账套
	 */
	public DebugLog(String url, String man, String master) {
		this.dl_id = StringUtil.getRandomString(10);
		this.dl_url = url;
		this.dl_man = man;
		this.dl_step = 0;
		this.dl_time = System.currentTimeMillis();
		this.dl_date = this.dl_time;
		this.dl_master = master;
	}

	/**
	 * 是否已超时
	 * 
	 * @return
	 */
	public boolean isTimeout() {
		return System.currentTimeMillis() - this.dl_time > getTimeLimitMillis();
	}

	private int getTimeLimitMillis() {
		// 下载请求设置20分钟上限
		if ("/ERP/common/downloadbyId.action".equals(this.dl_url) || "/ERP/common/download.action".equals(this.dl_url)
				|| "/ERP/common/excel/create.xls".equals(this.dl_url) || "/ERP/common/excel/grid.xls".equals(this.dl_url)) {
			return 1200000;
		}
		// 普通请求设置5分钟上限
		return 300000;
	}

}
