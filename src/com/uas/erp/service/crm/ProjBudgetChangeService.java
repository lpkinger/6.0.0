package com.uas.erp.service.crm;



public interface ProjBudgetChangeService {
	void saveProjBudgetChange(String formStore,String caller);
	void deleteProjBudgetChange(int pbc_id,String caller);
	void updateProjBudgetChange(String formStore,String caller);
	void auditProjBudgetChange(int pbc_id,String caller);
	void resAuditProjBudgetChange(int pbc_id,String caller);
	void submitProjBudgetChange(int pbc_id,String caller);
	void resSubmitProjBudgetChange(int pbc_id,String caller);
}
