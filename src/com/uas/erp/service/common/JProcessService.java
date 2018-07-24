package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JNodeEfficiency;
import com.uas.erp.model.JProcess;

public interface JProcessService {
	List<JProcess> getJProcessList(int page, int pageSize);
	int getJProcessCount();
	void delete(String ids);
	List<JProcess> search(String condition, int page, int pageSize);
	int searchCount(String condition);
    Map<String,Object> getJprocessNode(String caller, int keyValue,String type);
	List<JProcess> getReviewedJProcessList(int page, int pageSize);
	List<JNodeEfficiency> getJNodeEfficiencysList(int page, int pageSize);
	List<JNodeEfficiency> searchJNodeEfficiency(String condition, int page, int pageSize);
	Map<String, Integer> getDuedateOfJNodeInProcessInstance(String processInstanceId);
	List<JNodeEfficiency> getTimeoutNodeList(int page, int pageSize);
	List<JNodeEfficiency> searchTimeoutJNode(String condition,int page, int pageSize);
	List<Map<String,Object>> SetCurrentJnodes(String caller, int keyValue);
	void updateJnodePerson(String param,String caller, Integer keyValue, Employee employee);
}
