package com.uas.erp.service.crm;



public interface ResearchProjectService {
	void saveResearchProject(String formStore,String gridStore,String caller);
	void deleteResearchProject(int pp_id,String caller);
	void updateResearchProject(String formStore,String gridStore,String caller);
	void auditResearchProject(int pp_id,String caller);
	void resAuditResearchProject(int pp_id,String caller);
	void submitResearchProject(int pp_id,String caller);
	void resSubmitResearchProject(int pp_id,String caller);
}
