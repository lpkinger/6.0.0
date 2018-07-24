package com.uas.erp.service.scm;

public interface PreCustomerService {
	void savePreCustomer(String formStore);
	void updatePreCustomerById(String formStore);
	void deletePreCustomer(int cu_id);
	void printPreCustomer(int cu_id);
	void auditPreCustomer(int cu_id);
	void resAuditPreCustomer(int cu_id);
	void submitPreCustomer(int cu_id);
	void resSubmitPreCustomer(int cu_id);
	int turnCustomer(int cu_id);
}
