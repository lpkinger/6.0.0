package com.uas.erp.service.scm;

public interface SaleClashService {
	void saveSaleClash(String formStore, String gridStore, String caller);
	void updateSaleClashById(String formStore, String gridStore, String caller);
	void deleteSaleClash(int sc_id, String caller);
	void printSaleClash(int sc_id, String caller);
	void auditSaleClash(int sc_id, String caller);
	void resAuditSaleClash(int sc_id, String caller);
	void submitSaleClash(int sc_id, String caller);
	void resSubmitSaleClash(int sc_id, String caller);
	void createSaleClash(Integer fromid, String fromcaller);
	void cancelSaleClash(Integer fromid, String fromcaller);
	void getSaleClash(Integer fromid, String fromcaller);
	void createForeCastClash(Integer sf_id) ; 
}
