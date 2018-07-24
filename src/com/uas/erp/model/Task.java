package com.uas.erp.model;

import java.io.Serializable;

public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String taskcode;
	private String name;
	private String attachs;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTaskcode() {
		return taskcode;
	}

	public void setTaskcode(String taskcode) {
		this.taskcode = taskcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttachs() {
		return attachs;
	}

	public void setAttachs(String attachs) {
		this.attachs = attachs;
	}

}
