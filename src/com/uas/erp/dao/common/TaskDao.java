package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.uas.erp.model.Employee;
import com.uas.erp.model.ProjectTask;
import com.uas.erp.model.Task;
import com.uas.erp.model.TaskTemplate;

public interface TaskDao {
	List<ProjectTask> getTasks(String condition);
	List<TaskTemplate> getTaskTemplateByParentId(int parentId,String condition);
	void saveProjectTask(String jsonData,String field,Object[] otherValues);
	void updateProjectTask(String jsonData,String keyfield);
	void deleteProjectTask(String jsonData,String keyfield);
	List<Map<String, Object>> getDependencies(String prjid);
	void saveDependency(String jsonData,String keyField,Object[]otherValues,String prjid);
	void updateDependency(String jsonData,String keyField);
	void deleteDependency(String jsonData,String keyField);
	void saveCheckList(String formStore);
	Task getTaskByCode(String code);
	//获得父级任务
	List<ProjectTask> getAllParentTasks(int id,String condition);
	Map<String,Object> saveAgenda(String addData, Employee employee, String language);
	void updateAgenda(String updateData, Employee employee, String language);
	void deleteAgenda(String deleteData, Employee employee, String language); 
}
