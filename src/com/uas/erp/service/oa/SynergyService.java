package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.Synergy;

public interface SynergyService {
	void saveSynergy(String formStore, String  caller);

	void deleteById(int parseInt);

	Synergy getSynergyById(int id);
	
	List<Synergy> getList(int page, int pageSize);
	
	int getListCount();
	
	List<Synergy> getByCondition(String condition, int page, int pageSize);
	
	int getSearchCount(String condition);

	void updateSynergy(String formStore, String  caller);

	void deleteSynergy(int id, String  caller);

	void submitSynergy(int id, String  caller);
}
