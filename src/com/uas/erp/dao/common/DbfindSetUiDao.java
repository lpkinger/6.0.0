package com.uas.erp.dao.common;

import com.uas.erp.model.DBFindSetUI;

public interface DbfindSetUiDao {
	/**
	 * @param caller
	 * @param field
	 * @param sob 帐套信息
	 * @return
	 */
	DBFindSetUI getDbFindSetUIByField(String caller, String field, String sob);
	DBFindSetUI getDbFindSetUIByCallerAndField(String caller, String field);
	DBFindSetUI getDbFindSetUIById(int id);
	void deleteDbFindSetUIById(int id);
}
