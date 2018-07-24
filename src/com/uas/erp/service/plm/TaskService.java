package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Task;

public interface TaskService {
	void saveTask(String formStore, String gridStore, String language, Employee employee);

	void saveBillTask(String formStore, String language, Employee employee);

	void deleteTask(int id, String language, Employee employee);

	void deleteDetail(int id, String language, Employee employee);

	void updateTaskById(String formStore, String gridStore, String language, Employee employee);

	void auditTask(int id, String language, Employee employee);

	void submitTask(int id, String lauguage, Employee employee);

	void resSubmitTask(int id, String language, Employee employee);

	void resAuditTask(int id, String language, Employee employee);

	List<JSONTree> getJSONMember(int id, String language);

	Task getTaskByCode(String code);

	void insertTask(String formStore, String param, Employee employee, String language);

	void updateTaskName(String name, int id);

	void vastReSubmitActive(String language, Employee employee, String caller, String data);

	Map<String, Object> getTaskInfo(String language, Employee employee, int taskId);

	Map<String, Object> saveAgenda(String formStore);

	String getMyAgenda(String emcode, String condition);

	void deleteAgenda(int id);

	void updateAgenda(String formStore);
	List<JSONObject> getFormTasks(String caller, String codevalue);
	void saveFormTask(String formStore, String caller, String codeValue);
	void updateFormTask(String formStore);
	void addScheduleTask(String title, String context);

	void saveTaskInterceptor(String data);

	void deleteTaskInterceptor(String data);

	void checkTaskInterceptor(String data);
}
