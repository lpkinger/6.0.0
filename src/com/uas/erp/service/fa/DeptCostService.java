package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.GridColumns;

public interface DeptCostService {

	List<Map<String, Object>> getDeptCost(String condition);

	List<Map<String, Object>> getColumn(String condition);

	/**
	 * 按部门编号生成动态grid列
	 * 
	 * @param condition
	 * @return
	 */
	List<GridColumns> getGridColumnsByDepts(String condition);

	List<Map<String, Object>> getEmplCost(String condition);

	List<Map<String, Object>> getEmplColumn(String condition);

}
