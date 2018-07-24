package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Synergy;

public interface SynergyDao {
	
	void delete(int sy_id);
	List<Synergy> getByCondition(String condition, int page, int pageSize);
	int getSearchCount(String condition);
	Synergy getSynergyById(int id);
	List<Synergy> getList(int page, int pageSize);
	int getListCount();
}
