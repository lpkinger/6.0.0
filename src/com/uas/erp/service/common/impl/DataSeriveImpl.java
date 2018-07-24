package com.uas.erp.service.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.common.DataDao;
import com.uas.erp.model.DataColumn;
import com.uas.erp.model.DataIndex;
import com.uas.erp.service.common.DataService;

/**
 * 标准界面的基本逻辑 
 */
@Service
public class DataSeriveImpl implements DataService{
	@Autowired
	private DataDao dataDao;
	
	@Override
	public void tidy(){
		List<DataColumn> list1 = dataDao.insertDD();
		List<DataColumn> list2 = dataDao.createTables();
		List<DataColumn> list3 = dataDao.alterTable();
		List<DataColumn> list4 = dataDao.insertDDD();
	}
	@Override
	public List<DataColumn> insertDD() {
		List<DataColumn> list = dataDao.insertDD();
		return list;
	}
	@Override
	public List<DataColumn> createTables() {
		List<DataColumn> list = dataDao.createTables();
		return list;
	}
	@Override
	public List<DataColumn> alterTable() {
		List<DataColumn> list = dataDao.alterTable();
		return list;
	}
	@Override
	public List<DataColumn> insertDDD() {
		List<DataColumn> list = dataDao.insertDDD();
		return list;
	}
	@Override
	public List<DataColumn> eqType() {
		List<DataColumn> list = dataDao.eqType();
		return list;
	}
	@Override
	public List<DataColumn> getDetailByTablename(String tablename) {
		List<DataColumn> list = dataDao.getDetailByTablename(tablename);
		return list;
	}
	
	@Override
	public List<DataColumn> getPropertyByTablename(String tablename) {
		List<DataColumn> list = dataDao.getPropertyByTablename(tablename);
		return list;
	}
	
	@Override
	public void createTable(String[] tablenames) {
		dataDao.createTables(tablenames);		
	}
	
	@Override
	public List<DataColumn> test(int start, int limit) {
		return dataDao.test(start, limit);
	}
	@Override
	public int testGetTotal() {
		return dataDao.getTotal();
	}
	
	@Override
	public List<DataColumn> add(int start, int limit, int i) {
		return dataDao.add(start, limit, i);
	}
	
	@Override
	public int getTotal(int i) {
		return dataDao.getTotal(i);
	}
	
	@Override
	public String addFields(String fields) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(fields);
		return dataDao.addFields(maps);
	}
	
	@Override
	public void alter(String fields) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(fields);
		dataDao.alter(maps);
	}
	@Override
	public List<DataIndex> getColumnIndexByTablename(String tablename) {
		// TODO Auto-generated method stub
		List<DataIndex> list = dataDao.getColumnIndexByTablename(tablename);
		return list;
	}
}
