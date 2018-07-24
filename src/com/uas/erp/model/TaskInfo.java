package com.uas.erp.model;

import java.util.Date;

/**
 * 导入PROJECT task信息分析
 * */
public class TaskInfo {
	private int task_id;
	private int fake_id;
	private int parent_id;
	private String task_name;
	private double task_duration;
	private String task_predecessors;
	private Date task_start_date;
	private Date task_end_date;
	private int real_task_id;
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public int getFake_id() {
		return fake_id;
	}
	public void setFake_id(int fake_id) {
		this.fake_id = fake_id;
	}
	public int getParent_id() {
		return parent_id;
	}
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public double getTask_duration() {
		return task_duration;
	}
	public void setTask_duration(double task_duration) {
		this.task_duration = task_duration;
	}
	
	public String getTask_predecessors() {
		return task_predecessors;
	}
	public void setTask_predecessors(String task_predecessors) {
		this.task_predecessors = task_predecessors;
	}
	public Date getTask_start_date() {
		return task_start_date;
	}
	public void setTask_start_date(Date task_start_date) {
		this.task_start_date = task_start_date;
	}
	public Date getTask_end_date() {
		return task_end_date;
	}
	public void setTask_end_date(Date task_end_date) {
		this.task_end_date = task_end_date;
	}
	public int getReal_task_id() {
		return real_task_id;
	}
	public void setReal_task_id(int real_task_id) {
		this.real_task_id = real_task_id;
	}
	
	
}
