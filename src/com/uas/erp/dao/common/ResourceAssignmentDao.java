package com.uas.erp.dao.common;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface ResourceAssignmentDao {
	JSONArray  getData(String caller, String condition);
	JSONObject getResourceData(String prjplanid);
	JSONArray getTaskResourceData(String caller, String condition);
	JSONArray getTaskAssignmentData(String caller, String condition);
	void saveAssignment(String jsonData,String keyField,Object otherValues[]);
	void updateAssignment(String jsonData,String keyField);
	void deleteAssignment(String jsonData,String keyField);
}
