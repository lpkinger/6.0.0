package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.Employee;
import com.uas.mobile.model.ListConditions;
import com.uas.mobile.model.ListView;
import com.uas.mobile.model.MobileQuery;
public interface ListService {
	ListView getListGridByCaller(String caller, String condition, int page, int pageSize, String orderby,
			Boolean _self, Integer _f, Employee employee, String currentMaster);
    List<ListConditions> getAllConditionsByCaller(String caller);
	List<MobileQuery> getMobileQuerys(Employee employee );
	List<Object> getCombByCaller(String caller, String field, Employee employee);
	List<Map<String,Object>> getAuditDetail(String caller);
	List<Map<String,Object>> getCombValueByCaller(String caller, String field);
	List<Map<Object, Object>> getDbfindGridByField(String caller, String field, String condition,
			int page, int pageSize);

	Map<String,Object> getFormAndGridData(String caller,String id,String isprocess,String config,HttpSession session);
	List<DBFindSetGrid> getGridDbfinds(String gridCaller,String gridField);
	
	public List<Map<String, Object>> getServices(String basePath, Employee employee, String kind ,String type, boolean noControl);
	
}