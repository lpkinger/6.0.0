package com.uas.erp.service.crm;



public interface ResearchService {
	void saveResearch(String formStore,String caller);
	void deleteResearch(int re_id,String caller);
	void updateResearch(String formStore,String caller);
}
