package com.uas.erp.service.common;

import java.util.List;

import com.uas.erp.model.DataColumn;
import com.uas.erp.model.DataIndex;


public interface DataService {
	void tidy();
	List<DataColumn> insertDD();
	List<DataColumn> createTables();
	List<DataColumn> alterTable();
	List<DataColumn> insertDDD();
	List<DataColumn> eqType();
	List<DataColumn> getDetailByTablename(String tablenam);
	List<DataColumn> getPropertyByTablename(String tablenam);
	void createTable(String[] tablenames);
	List<DataColumn> test(int start, int limit);
	int testGetTotal();
	List<DataColumn> add(int start, int limit, int i);
	int getTotal(int i);
	void alter(String fields);
	String addFields(String fields);
	List<DataIndex> getColumnIndexByTablename(String tablename);
}
