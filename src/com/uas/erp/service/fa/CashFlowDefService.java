package com.uas.erp.service.fa;



public interface CashFlowDefService {
	void saveCashFlowDef(String formStore, String caller);
	void updateCashFlowDefById(String formStore, String caller);
	void deleteCashFlowDef(int pa_id, String caller);

}
