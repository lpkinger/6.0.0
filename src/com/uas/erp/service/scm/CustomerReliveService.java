package com.uas.erp.service.scm;

public interface CustomerReliveService {
	void saveCustomerRelive(String formStore, String gridstore, String caller);

	void updateCustomerReliveById(String formStore, String gridstore, String caller);

	void deleteCustomerRelive(int cr_id, String caller);

	void auditCustomerRelive(int cr_id, String caller);

	void submitCustomerRelive(int cr_id, String caller);

	void resSubmitCustomerRelive(int cr_id, String caller);

	void countCustReturn();
}
