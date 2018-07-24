package com.uas.erp.service.drp;



public interface CustomerTypeService {
	void saveCustomer(String formStore,  String caller);
	boolean checkCustomerByEnId(int cu_enid, int cu_otherenid);
	void updateCustomer(String formStore,  String caller);
	void deleteCustomer(int cu_id,  String caller);
	void auditCustomer(int cu_id,  String caller);
	void resAuditCustomer(int cu_id,  String caller);
	void submitCustomer(int cu_id,  String caller);
	void resSubmitCustomer(int cu_id,  String caller);
	void bannedCustomer(int cu_id,  String caller);
	void resBannedCustomer(int cu_id,  String caller);
}
