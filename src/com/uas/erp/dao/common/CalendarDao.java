package com.uas.erp.dao.common;

public interface CalendarDao {
	void save(String addData,String keyField);
	  void update(String updateData,String keyField);
	  void delete(String deleteData,String keyField);
	  String getMyData(String emcode, String condition);
	  String getMyAgenda(String emid);
}
