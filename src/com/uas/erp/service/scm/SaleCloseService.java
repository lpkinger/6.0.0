package com.uas.erp.service.scm;

public interface SaleCloseService {
	void deleteSaleClose(int sc_id, String caller);
	void auditSaleClose( int sc_id,String caller);
	void resAuditSaleClose(int sc_id, String caller);
	void submitSaleClose(int sc_id, String caller);
	void resSubmitSaleClose(int sc_id, String caller);
}
