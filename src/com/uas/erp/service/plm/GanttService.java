package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.mpxj.ProjectFile;

public interface GanttService {
	List<JSONObject> getJsonGantt(String condition, String actul);
	JSONObject getData(String condition, Employee employee);
	void saveGantt(String jsonData);
	void updateGantt(String jsonData);
	void deleteGantt(String jsonData);
	List<Map<String, Object>> getDependencies(String prjid);
	void saveDependency(String jsonData,String condition);
	void updateDependency(String jsonData);
	void deleteDependency(String jsonData);
	/*void syncTask(String create, String update, String delete,String detnos, int prjId);
	void syncDependency(String create, String update, String remove,int prjId);
	void syncAssigns(String create, String update, String remove, int prjId);*/
	void activeTask(String data, int prjId);
	void endTask(int data, int prjId);
	boolean ImportMpp(int id, ProjectFile pf);
	void sync(String taskcreate, String taskupdate, String taskremove,
			String assigncreate, String assginupdate, String assginremove,
			String dependencycreate, String dependencyupdate,
			String dependencyremove, String detnos, int prjId);
	void setDoc(int prjId, int taskId, String docName, String docId);
	Map<String, Object> getLogByCondition(String prjplanid, String docname,
			int page, int start, int limit);
	List<Map<String,Object>> getProjectPhase(String condition, String checked);
	void linkPhase(String prjId, String phaseid,String phase, String taskId,String detno);
	
}
