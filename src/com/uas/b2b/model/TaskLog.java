package com.uas.b2b.model;

import java.util.Date;

import org.springframework.http.HttpStatus;

import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.dao.Saveable;

/**
 * 平台数据传输日志
 * 
 * @author yingp
 *
 */
public class TaskLog implements Saveable {

	private Date log_date;
	private String log_title;
	private Integer data_size;
	private String response_text;
	private int response_status;

	public Date getLog_date() {
		return log_date;
	}

	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}

	public String getLog_title() {
		return log_title;
	}

	public void setLog_title(String log_title) {
		this.log_title = log_title;
	}

	public Integer getData_size() {
		return data_size;
	}

	public void setData_size(Integer data_size) {
		this.data_size = data_size;
	}

	public String getResponse_text() {
		return response_text;
	}

	public void setResponse_text(String response_text) {
		this.response_text = response_text;
	}

	public int getResponse_status() {
		return response_status;
	}

	public void setResponse_status(int response_status) {
		this.response_status = response_status;
	}

	/**
	 * @param title
	 *            描述
	 * @param dataSize
	 *            数据量
	 * @param response
	 *            返回结果
	 */
	public TaskLog(String title, int dataSize, Response response) {
		this.log_date = new Date();
		this.log_title = title;
		this.data_size = dataSize;
		this.response_status = response.getStatusCode();
		if (response.getStatusCode() != HttpStatus.OK.value())
			this.response_text = response.getResponseText();
	}

	@Override
	public String table() {
		return "log$b2btask";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
