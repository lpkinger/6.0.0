package com.uas.erp.service.fa;

public interface CustomerBaseFPService {
	void saveCustomerFP(String formStore, String gridStore, String caller);

	boolean checkCustomerFPByEnId(int cu_enid, int cu_otherenid);

	void updateCustomerFP(String formStore, String gridStore, String caller);

	void updateCustomerFPCreditSet(String formStore, String caller);

	void deleteCustomerFP(int cu_id, String caller);

	void auditCustomerFP(int cu_id, String caller);

	void resAuditCustomerFP(int cu_id, String caller);

	void submitCustomerFP(int cu_id, String caller);

	void resSubmitCustomerFP(int cu_id, String caller);

	void bannedCustomerFP(int cu_id, String caller);

	void resBannedCustomerFP(int cu_id, String caller);

	void submitHandleHangCustomerBaseFP(int cu_id, String caller);

	void HandleHangCustomerBaseFP(int cu_id, String caller);
}
