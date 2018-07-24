package com.uas.erp.service.drp;



public interface SalePriceApplyService {
	void saveSalePriceApply(String formStore, String gridStore,  String caller);
	void updateSalePriceApplyById(String formStore, String gridStore,  String caller);
	void deleteSalePriceApply(int sp_id,  String caller);
	void printSalePriceApply(int sp_id,  String caller);
	void auditSalePriceApply(int sp_id,  String caller);
	void resAuditSalePriceApply(int sp_id,  String caller);
	void submitSalePriceApply(int sp_id,  String caller);
	void resSubmitSalePriceApply(int sp_id,  String caller);
}
