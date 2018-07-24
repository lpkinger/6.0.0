package com.uas.erp.service.crm;



public interface ProdAnalyseService {
	void saveProdAnalyse(String formStore,String caller);
	void deleteProdAnalyse(int pa_id,String caller);
	void updateProdAnalyse(String formStore,String caller);
	void auditProdAnalyse(int pa_id,String caller);
	void resAuditProdAnalyse(int pa_id,String caller);
	void submitProdAnalyse(int pa_id,String caller);
	void resSubmitProdAnalyse(int pa_id,String caller);
}
