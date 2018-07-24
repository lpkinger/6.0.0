package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.DataList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridPanel;

public interface DataListService {
	/**
	 * 取列表配置及数据
	 * 
	 * @param caller
	 * @param condition
	 * @param page
	 * @param pageSize
	 * @param orderby
	 * @param _self
	 * @param _f
	 * @param isCloud
	 * @return
	 */
	GridPanel getDataListGridByCaller(String caller, String condition, int page, int pageSize, String orderby, Boolean _self, Integer _f,
			boolean fromHeader,boolean isCloud, boolean _jobemployee);

	/**
	 * 取列表分页数据
	 * 
	 * @param caller
	 * @param condition
	 * @param page
	 * @param pageSize
	 * @param orderBy
	 * @param _self
	 * @param _alia
	 * @param _f
	 * @param isCloud
	 * @return
	 */
	Map<String, Object> getDataListData(String caller, String condition, int page, int pageSize, String orderBy, Boolean _self,
			boolean _alia, Integer _f, boolean isCloud, boolean _jobemployee);
	int getCountByCaller(String caller, String condition,Boolean _self,boolean fromHeader, boolean isCloud, boolean _jobemployee);

	String appendCondition(DataList dataList, String condition, Employee employee);
	
	String appendPowerCondition(DataList dataList, String condition, Employee employee,Boolean _self, boolean _jobemployee);
	
	void vastDelete(String language, Employee employee, String caller, int[] id);

	void vastSubmit(String language, Employee employee, String caller, int[] id);

	void vastAudit(String language, Employee employee, String caller, int[] id);

	void vastSend(String language, Employee employee, String caller, int[] id);

	void vastFreeze(String language, Employee employee, String caller, int[] id);

	void vastResStart(String language, Employee employee, String caller, int[] id);

	void vastSave(String language, Employee employee, String caller, String data);

	void vastClose(String language, Employee employee, String caller, int[] id);

	void vastPost(String language, Employee employee, String caller, int[] id);

	void vastResPost(String language, Employee employee, String caller, int[] id);

	void vastCancel(String language, Employee employee, String caller, String data);

	void AgreeToPrice(String language, Employee employee, String caller, int[] id);

	void NotAgreeToPrice(String language, Employee employee, String caller, int[] id);

	void saveTemplate(String caller, String desc, String fields, Employee employee);

	void AgreeAllToPrice(String language, Employee employee, String caller, int id);

	void saveEmpsDataListDetails(String caller, String data, Employee employee);

	void resetEmpsDataListDetails(String caller);

	void vastConfirmPeriods(String caller, String data);

	void vastConfirmFirstPeriods(String caller, String data);

	Map<String, Object> getComboDatalist(String condition, int page, int start, int limit, String sort);
	
	GridPanel getColumns(String caller, boolean isCloud);
	List<Map<String, Object>>getDataListFilterName(String caller);
	List<Map<String, Object>>getTreeNodeData(String id);
	Boolean deleteTreeNode(String id);
	void saveQuery(int id, String data, boolean isDefalut, String caller);
	void saveAnotherQuery(String queryName, boolean isDefault, boolean isNormal, String data, String caller);
	boolean setDefault(String id,String caller);
	boolean hasFilterCondition(String caller);
}
