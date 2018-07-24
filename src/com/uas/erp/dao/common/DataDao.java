package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.DataColumn;
import com.uas.erp.model.DataIndex;

public interface DataDao {
	
	List<DataColumn> insertDD();
	List<DataColumn> createTables();
	List<DataColumn> insertDDD();
	List<DataColumn> alterTable();
	List<DataColumn> eqType();
	List<DataColumn> getDetailByTablename(String tablename);
	List<DataColumn> getPropertyByTablename(String tablename);
	void createTables(String[] tablenames);
	List<DataColumn> test(int start, int limit);
	int getTotal();
	List<DataColumn> add(int start, int limit, int i);
	int getTotal(int i);
	String addFields(List<Map<Object, Object>> maps);
	void alter(List<Map<Object, Object>> maps);
	List<DataIndex> getColumnIndexByTablename(String tablename);
}
