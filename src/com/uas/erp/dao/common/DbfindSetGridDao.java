package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.DBFindSetGrid;

public interface DbfindSetGridDao {
	List<DBFindSetGrid> getDbFindSetGridsByCaller(String caller);
	void deleteDbFindSetGridById(int id);
}
