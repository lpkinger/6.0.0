package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JProcessSet;

public interface JProcessSetService {
	Map<String, Object> getFormDataByformCondition(String formCondition);
	void saveJProcessSet(String caller, String formStore, String param, String language, Employee employee);
	void deleteJProcessSet(String caller, int id, String language,Employee  employee);
	void updateJProcessSetById(String caller, String formStore, String param,String language,Employee employee);
	void saveJprocessTemplate(String formStore, String clobtext,String language,
			 Employee employee);
	void updateJprocessTemplate(String formStore, String clobtext,
			String language, Employee employee);
	void deleteJprocessTemplate(int id, String language, Employee employee);
	void auditJprocessTemplate(int id, String language, Employee employee);
	void resAuditJprocessTemplate(int id, String language, Employee employee);
	void submitJprocessTemplate(int id, String language, Employee employee);
	void resSubmitJprocessTemplate(int id, String language, Employee employee);
	void saveAutoJprocess(String formStore, String clobtext, String language,
			Employee employee);
	void updateAutoJprocess(String formStore, String clobtext, String language,
			Employee employee);
	void deleteAutoJprocess(int id, String language, Employee employee);
	void auditAutoJprocess(int id,String caller,String language,  Employee employee);
	void resAuditAutoJprocess(int id,String caller,String language, Employee employee);
	void submitAutoJprocess(int id,String caller,String language, Employee employee);
	void resSubmitAutoJprocess(int id,String caller,String language, Employee employee);
	List<JSONObject> ProcessQueryPersons(String likestring);
	JProcessSet getJprocessSet(String caller);
	List<JSONObject>  ProcessQueryAgentPersons(String likestring);
}
