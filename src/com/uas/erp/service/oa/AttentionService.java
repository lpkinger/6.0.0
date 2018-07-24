package com.uas.erp.service.oa;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.JSONTree;

public interface AttentionService {
 void deleteAttentionGrade(String data,String caller);
 void saveAttentionGrade(String formStore, String  caller);
 void saveAttentionSub(String caller,String formStore, String param, String mutiselected);
 JSONArray getData(String caller,String condition,int page,int pageSize);
 GridPanel getGridPanel(String caller);
 JSONArray getAttentionsByEmId(String caller,int emid);
 Map<String, Object> getEmployeeData(String caller,int emid);
 String ChekOnlineEmployee(int emid);
 void deleteAttentions(String data, String caller);
 JSONArray getAaccreditData(String caller, String condition);
 List<JSONTree>  getEmployees(String caller,int emid);
 int  getAttentionCounts(String caller);
 Map<String, Object> getEmployeeDataByParam(String caller,String param);
 JSONArray getAttentionDataByParam(String param, String caller);
}
