package com.uas.erp.service.sys;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface AlertDataService {
	
	public void revert(int id, String caller,String AD_CAUSE,String AD_SOLUTION);
	public void confirm(int id, String caller,String AD_CAUSE,String AD_SOLUTION);
	void dealRevert(String caller,String data);
	List<Map<String, Object>> getAlertData(Employee employee,String condition,String likestr,Integer page,Integer pageSize);
//	public int getAlertDataTotal(Employee employee,String condition,String likestr, Integer page, Integer pageSize) ;
//	Map<String, Object> getAlertDataCount(Employee employee) ;
	
}
