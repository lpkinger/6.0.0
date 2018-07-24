package com.uas.erp.service.scm;

public interface CustTurnService {

	void saveCustTurn(String formStore, String gridStore, String caller);
	void deleteCustTurn(int ct_id, String caller);
	void updateCustTurn(String formStore, String gridStore,
			String caller);
	void submitCustTurn(int ct_id, String caller);
	void resSubmitCustTurn(int ct_id, String caller);
	void auditCustTurn(int ct_id, String caller);
	void resAuditCustTurn(int ct_id, String caller);
}
