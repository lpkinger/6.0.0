package com.uas.erp.service.scm;

public interface CustomerChangeService {
	void saveCustomerChange(String formStore, String caller);

	void deleteCustomerChange(int id, String caller);

	void updateCustomerChange(String formStore, String caller);

	void auditCustomerChange(int id, String caller);

	void submitCustomerChange(int id, String caller);

	void resSubmitCustomerChange(int id, String caller);

	void resAuditCustomerChange(int id, String caller);
}
