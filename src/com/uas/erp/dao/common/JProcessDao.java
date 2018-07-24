package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;


import com.uas.erp.model.Employee;
import com.uas.erp.model.JProcess;
import com.uas.erp.model.JnodeRelation;


public interface JProcessDao {
	
	List<JProcess> getAllJProcess(int page, int pageSize);
	int getAllJProcessCount();
	void delete(int id);
	List<JProcess> search(String condition, int page, int pageSize);
	int searchCount(String condition);
	List<JProcess> getAllReviewedJProcess(int page, int pageSize);
	long getDurationOfInProcessInstance(String pInstanceId);
	int getSumOfNode(String pInstanceId);
	Map<String,Integer> getDuedateOfJNode(String processDefId);
	Map<String, Object> getDecisionConditionData(String caller,int keyValue);
	List<JnodeRelation> getJnodeRelationsByDefId(String DefId);
	void saveJprocessTemplate(String formStore, String clobtext,
			String language, Employee employee);
	void updateJprocessTemplate(String formStore, String clobtext,
			String language, Employee employee);
	void saveAutoJprocess(String formStore, String clobtext, String language,
			Employee employee);
	void updateAutoJprocess(String formStore, String clobtext, String language,
			Employee employee);
}
