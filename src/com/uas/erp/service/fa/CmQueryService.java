package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;


import com.uas.erp.model.GridPanel;

public interface CmQueryService {
	GridPanel getCmQuery(String condition);

	List<Map<String, Object>> getSmQuery(String condition);

	List<Map<String, Object>> getCmDetailQuery(String condition);

	List<Map<String, Object>> getCmDetailById(String condition);

	List<Map<String, Object>> getCmDetailByIdDetail(String condition);

	void refreshCmQuery(int yearmonth);
	
	void refreshQuery(String condition);
	
	
	
	GridPanel getCmCopQuery(String condition);

	List<Map<String, Object>> getCmCopDetailQuery(String condition);

	List<Map<String, Object>> getCmCopDetailById(String condition);

	List<Map<String, Object>> getCmCopDetailByIdDetail(String condition);

	void refreshCmCopQuery(int yearmonth);
}
