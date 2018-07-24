package com.uas.erp.service.scm;

public interface SaleProjectService {
	void saveSaleProject(String formStore, String caller);
	void updateSaleProjectById(String formStore, String caller);
	void deleteSaleProject(int sp_id, String caller);
	void auditSaleProject(int sp_id, String caller);
	void resAuditSaleProject(int sp_id, String caller);
	void submitSaleProject(int sp_id, String caller);
	void resSubmitSaleProject(int sp_id, String caller);
	int turnProject(int sp_id, String caller);
	
}
