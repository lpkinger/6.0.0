package com.uas.erp.service.scm;

public interface SaleChangeService {
	void saveSaleChange(String caller, String formStore, String gridStore);
	void updateSaleChangeById(String caller, String formStore, String gridStore);
	void deleteSaleChange(String caller, int sc_id);
	void auditSaleChange( int sc_id, String caller);
	void resAuditSaleChange(String caller, int sc_id);
	void submitSaleChange(String caller, int sc_id);
	void resSubmitSaleChange(String caller, int sc_id);
	String[] printSaleChange(String caller, int sa_id,String reportName,String condition);
}
