package com.uas.erp.service.crm;



public interface CompetitorActivityplanService {
	void saveCompetitorActivityplan(String formStore,String caller);
	void deleteCompetitorActivityplan(int cap_id,String caller);
	void updateCompetitorActivityplan(String formStore,String caller);
}
