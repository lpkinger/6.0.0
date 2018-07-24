package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Agenda;

public interface AgendaDao {
	
	void delete(int ag_id);
	List<Agenda> getByCondition(String condition, int page, int pageSize);
	List<Agenda> getArrangeList(int em_id, int page, int pageSize);
	int getArrangeListCount(int em_id);
	int getSearchCount(String title);
	Agenda getAgendaById(int id);
	List<Agenda> getList(int em_id, int page, int pageSize);
	int getListCount(int em_id);
}
