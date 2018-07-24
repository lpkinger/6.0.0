package com.uas.erp.service.crm;



public interface CompetitorService {
	void saveCompetitor(String formStore, String param,String caller);
	void deleteCompetitor(int co_id,String caller);
	void updateCompetitorById(String formStore, String param,String caller);

}
