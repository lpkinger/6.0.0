package com.uas.erp.dao.common;

import com.uas.erp.model.DBFindSet;

public interface DbfindSetDao {
	DBFindSet getDbfind(String caller, String sob);
}
