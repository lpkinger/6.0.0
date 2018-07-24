package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

import oracle.net.aso.e;

public interface MessageCenterService {

	Map<String, Object> getCount(Employee employee,String timestr) ;
	List<Map<String, Object>> getMessageData(Employee employee,String condition,String likestr,Integer page,Integer pageSize);
	Map<String, Object> getTaskData(Employee employee,String condition, String fields,String likestr,Integer page,Integer limit,String  type);
	Map<String, Object> getProcessData(Employee employee,String type, String likestr,Integer page,Integer limit);
	Map<String, Object> getFlowData(Employee employee,String type, String likestr,Integer page,Integer limit);
	public int getMessageTotal(Employee employee,String condition,String likestr, Integer page, Integer pageSize) ;
	Boolean getMessageContent(Employee employee,Integer id,String master);
	Boolean updateReadstatus(String data);
	Map<String, Object> getmessageCount(Employee employee) ;
	Object getFieldData(String caller, String field, String condition);
	
	List<Map<String, Object>> searchData(Employee employee,String condition,String type,String filed);
	
}
