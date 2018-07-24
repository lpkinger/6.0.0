package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.DataList;
import com.uas.erp.model.Employee;

public interface DataListDao {
	/**
	 * @param caller
	 * @param sob
	 *            帐套名称
	 * @return
	 */
	DataList getDataList(String caller, String sob);

	String getRelativesettings(String caller, String type, int emid);

	DataList getDataListByEm(String caller, Employee employee);

	/**
	 * 按datalist配置查询数据
	 * 
	 * @param dataList
	 * @param condition
	 * @param employee
	 * @param page
	 * @param pageSize
	 * @param _f
	 * @param _alia
	 * @param orderby
	 * @param jobemployee 
	 * @return
	 */
/*	public List<Map<String, Object>> getDataListData(DataList dataList, String condition, Employee employee, int page, int pageSize,
			Integer _f, boolean _alia, String orderby);*/

	public List<Map<String, Object>> getDataListData(DataList dataList, String condition, Employee employee, int page, int pageSize,
			Integer _f, boolean _alia, String orderby,boolean jobemployee);
	
	/**
	 * 按datalist配置查询数据
	 * 
	 * @param dataList
	 * @param condition
	 * @param employee
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String getDataStringByDataList(DataList dataList, String condition, Employee employee, int page, int pageSize);

	List<Map<String, Object>> getSummaryData(DataList datalist, String condition,boolean jobemployee);
	
	String getSqlWithJobEmployee(Employee employee);
}
