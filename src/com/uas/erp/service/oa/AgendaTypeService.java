package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.AgendaType;

public interface AgendaTypeService {
	
	void saveAgendaType(String formStore, String caller);
	void updateAgendaType(String formStore, String caller);
	void deleteAgendaType(int at_id, String caller);
	List<AgendaType> getAll(int page, int pageSize);
	List<AgendaType> searchByName(String name, int page, int pageSize);
	void deleteById(int id);
	AgendaType getById(int id);
	int getSearchCount(String name);
	int getAllCount(String caller);
	void vastDeleteAgendaType(int[] id, String caller);

}
