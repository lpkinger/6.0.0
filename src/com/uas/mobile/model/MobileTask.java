package com.uas.mobile.model;

import java.io.Serializable;
import java.util.Date;

public class MobileTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private String recorder;
	private String resourcecode;
	private String resourcename;
	private Date taskdate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRecorder() {
		return recorder;
	}
	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}
	public String getResourcecode() {
		return resourcecode;
	}
	public void setResourcecode(String resourcecode) {
		this.resourcecode = resourcecode;
	}
	public String getResourcename() {
		return resourcename;
	}
	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}
	public Date getTaskdate() {
		return taskdate;
	}
	public void setTaskdate(Date taskdate) {
		this.taskdate = taskdate;
	}
	
}
