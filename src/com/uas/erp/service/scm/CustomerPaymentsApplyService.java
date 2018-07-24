package com.uas.erp.service.scm;

public interface CustomerPaymentsApplyService {
	void saveCustomerPaymentsApply(String formStore,String gridStore, String caller);
	void deleteCustomerPaymentsApply(int ca_id, String caller);
	void updateCustomerPaymentsApply(String formStore,String gridStore, String caller);
	void submitCustomerPaymentsApply(int ca_id, String caller);
	void resSubmitCustomerPaymentsApply(int ca_id, String caller);
	void auditCustomerPaymentsApply(int ca_id, String caller);
	void resAuditCustomerPaymentsApply(int ca_id, String caller);
}
