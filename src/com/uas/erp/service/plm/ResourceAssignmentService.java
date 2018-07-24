package com.uas.erp.service.plm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface ResourceAssignmentService {
   JSONArray  getData(String caller,String condition);
   JSONArray  getTaskResourceData(String caller,String condition);
   JSONArray  getTaskAssignmentData(String caller,String condition);
   JSONObject getResourceData(String prjplanid);
   void saveAssignment(String jsonData);
   void updateAssignment(String jsonData);
   void deleteAssignment(String jsonData);
}
