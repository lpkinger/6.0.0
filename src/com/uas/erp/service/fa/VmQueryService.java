package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;


import com.uas.erp.model.GridPanel;


public interface VmQueryService {
	GridPanel getVmQuery(String caller, String condition);
	List<Map<String, Object>> getVmDetailQuery(String condition);
	List<Map<String, Object>> getVmDetailById(String condition);
	List<Map<String, Object>> getVmDetailByIdDetail(String condition);
	void refreshVmQuery(int yearmonth);
	
	GridPanel getVmCopQuery(String caller, String condition);
	List<Map<String, Object>> getVmCopDetailQuery(String condition);
	List<Map<String, Object>> getVmCopDetailById(String condition);
	List<Map<String, Object>> getVmCopDetailByIdDetail(String condition);
	void refreshVmCopQuery(int yearmonth);
}
