package com.uas.erp.service.ma;

import com.uas.erp.model.DataListWrap;

public interface MADataListService {
	void save(String form, String formdetail);

	void update(String form, String add, String update, String del);

	void delete(int id);

	boolean checkCaller(String caller);

	String resetCombo(String caller, String field);

	/**
	 * 导出
	 * 
	 * @param dl_id
	 * @return
	 */
	DataListWrap exportDataList(Integer dl_id);

	/**
	 * 导入
	 * 
	 * @param dataListWrap
	 */
	void importDataList(DataListWrap dataListWrap);
	
	String copy(int id, String newCaller);
}
