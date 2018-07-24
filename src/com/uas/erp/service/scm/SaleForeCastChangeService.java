package com.uas.erp.service.scm;


public interface SaleForeCastChangeService {
	void saveSaleForeCastChange(String formStore, String gridStore, String caller);

	void updateSaleForeCastChangeById(String formStore, String gridStore, String caller);

	void deleteSaleForeCastChange(int sc_id, String caller);

	void printSaleForeCastChange(int sc_id, String caller);

	void auditSaleForeCastChange(int sc_id, String caller);

	void resAuditSaleForeCastChange(int sc_id, String caller);

	void submitSaleForeCastChange(int sc_id, String caller);

	void resSubmitSaleForeCastChange(int sc_id, String caller);
}
