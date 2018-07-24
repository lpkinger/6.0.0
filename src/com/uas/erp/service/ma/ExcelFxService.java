package com.uas.erp.service.ma;


public interface ExcelFxService {
	void save(String formStore, String  caller);
	void delete(int id,String  caller);
	void update(String formStore, String  caller);
}
