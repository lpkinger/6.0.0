package com.uas.erp.service.fa;



public interface CustomerCreWarnService {
	void saveCustomerCreWarn(String formStore, String gridStore, String caller);
	void updateCustomerCreWarnById(String formStore, String gridStore, String caller);
	void deleteCustomerCreWarn(int cu_id, String caller);
	void printCustomerCreWarn(int cu_id, String caller);
	void auditCustomerCreWarn(int cu_id, String caller);
	void resAuditCustomerCreWarn(int cu_id, String caller);
	void submitCustomerCreWarn(int cu_id, String caller);
	void resSubmitCustomerCreWarn(int cu_id, String caller);

}
