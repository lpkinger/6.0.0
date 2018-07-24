package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MobileInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3782110685684499061L;
	private Employee employee;
	private List<Map<String, Object>> flows;
	private List<Map<String, Object>> procands;
	private List<Map<String, Object>> tasks;
	private Integer procandCount;
	private Integer flowCount;
	private Integer taskCount;

	public List<Map<String, Object>> getFlows() {
		return flows;
	}

	public void setFlows(List<Map<String, Object>> flows) {
		this.flows = flows;
	}

	public List<Map<String, Object>> getTasks() {
		return tasks;
	}

	public void setTasks(List<Map<String, Object>> tasks) {
		this.tasks = tasks;
	}

	public Integer getFlowCount() {
		return flowCount;
	}

	public void setFlowCount(Integer flowCount) {
		this.flowCount = flowCount;
	}

	public Integer getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(Integer taskCount) {
		this.taskCount = taskCount;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<Map<String, Object>> getProcands() {
		return procands;
	}

	public void setProcands(List<Map<String, Object>> procands) {
		this.procands = procands;
	}

	public Integer getProcandCount() {
		return procandCount;
	}

	public void setProcandCount(Integer procandCount) {
		this.procandCount = procandCount;
	}

}
