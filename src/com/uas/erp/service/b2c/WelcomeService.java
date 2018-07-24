package com.uas.erp.service.b2c;


import com.uas.erp.model.Employee;

public interface WelcomeService {
	boolean getWelcomeStatus(Employee employee);
	public boolean setWelcomeStatus(Employee employee,String url);
	public String isTureMaster();
}
