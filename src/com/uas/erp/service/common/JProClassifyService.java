package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JProClassify;
import com.uas.erp.model.JProcessDeploy;
import com.uas.erp.model.JSONTree;

public interface JProClassifyService {
	List<JProClassify> getJProClassifies(int start,int limit);

	void saveJProClassify(String formStore, String param, String language,Employee employee);

	void deleteJProClassify(int id, String language, Employee employee);

	void updateJProClassifyById(String formStore, String param,String language, Employee employee);

	List<Object[]> getAllJProClassify(String language,Employee employee);

	void removeToOtherClassify(int id, String data, String language,
			Employee employee);
     Map<Object,Object> getAllJprocessDeployInfo();

	List<Map<Object,Object>> getProcessInfoByCondition(String condition);

	void orderByJprocess(String data, String language, Employee employee);

	List<JProcessDeploy> getJpTree(String condition);
}
