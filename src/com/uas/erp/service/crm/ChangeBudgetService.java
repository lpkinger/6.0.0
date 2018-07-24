package com.uas.erp.service.crm;



public interface ChangeBudgetService {
	void saveChangeBudget(String formStore, String gridStore,String caller);
	void updateChangeBudgetById(String formStore, String gridStore,String caller);
	void deleteChangeBudget(int cb_id,String caller);
	void auditChangeBudget(int cb_id,String caller);
	void resAuditChangeBudget(int cb_id,String caller);
	void submitChangeBudget(int cb_id,String caller);
	void resSubmitChangeBudget(int cb_id,String caller);
}
