package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.AgendaType;

public interface AgendaTypeDao {
	
	void save(AgendaType at);
	void delete(int at_id);
	List<AgendaType> getByName(String name, int page, int pageSize);
	List<AgendaType> getAll(int page, int pageSize);
	int getAllCount();
	int getSearchCount(String title);
	AgendaType getById(int id);
}
