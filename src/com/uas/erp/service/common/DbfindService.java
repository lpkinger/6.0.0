package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridPanel;

public interface DbfindService {
	GridPanel getDbfindGridByCaller(String caller, String condition, String orderby, int page, int pageSize, String language, boolean isCloud);
	int getCountByCaller(String caller, String condition, boolean isCloud , boolean autoDbfind);
	GridPanel getDbfindGridByField(String caller, String field, String condition, int page, int pageSize, boolean isCloud);
	int getCountByField(String caller, String field, String condition, boolean isCloud , boolean autoDbfind);
	JSONObject getDbFindSetUIByField(String caller, String field, String condition, boolean isCloud);
	String getDbFindFields(String table);
	void deleteDbFindField(String field, int id);
	int saveDbFindSetUI(String caller,String formStore, String gridStore);
	void deleteDbfindSetUI(int id);
	List<DBFindSetGrid> getDbFindSetGridByCaller(String caller, String field);
	JSONObject getDbFindSetGridFieldsByCallerAndFields(Employee employee, String caller,String field);
	void saveDbFindSetGrid(String caller, String field,String table,  String dgfield,String gridStore);
	void deleteDBFindSetGrid(int id);
	String getDlccallerByTable(String table,String fields);
	List<String> getSearchData(String table,String field,String condition,String configSearchCondition,String name,String caller,String type,String searchTpl);
	List<Map<String, Object>> getComboBoxTriggerData(String id,String text);
	Boolean saveToCommonWords(String value);
	Boolean deleteCommonWords(String id);
}
