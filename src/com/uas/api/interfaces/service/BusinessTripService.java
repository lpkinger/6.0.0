package com.uas.api.interfaces.service;

import java.util.List;
import java.util.Map;



public interface BusinessTripService {
	
	public Map<String,Object> UpdateOrInsertBussinessTrip(String emcode,String jsonStr,String master,int isLead,String otherContent);
	
	public List<Map<String,Object>> getEmployee(String emcode,String master);
	
	public boolean updateEmployee(String master,String params);
	
	public Map<String,Object> getDepartment(String dept);

	public boolean newDepartment(String dept,String deptname);
	
	public Map<String,Object> getEmp(String dept,String code);
}
