package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.Agenda;

public interface AgendaService {
	
//	void update(int ag_id);
	List<Agenda> getArrangeList(String caller, int page, int pageSize);
	List<Agenda> searchByCondition(String condition, int page, int pageSize);
	void deleteById(int ag_id);
	int getSearchCount(String name);
	int getArrangeListCount(String caller);
	Agenda getAgendaById(int id);
	List<Agenda> getList(String caller, int page, int pageSize);
	int getListCount(String callet);

}
