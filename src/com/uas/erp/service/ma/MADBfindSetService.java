package com.uas.erp.service.ma;

public interface MADBfindSetService {
	void save(String form, String formdetail);
	boolean checkCaller(String caller);
	void update(String formStore, String param);
	void delete(int id);
}
