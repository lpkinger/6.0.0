package com.uas.erp.service.scm;

public interface VePaymentsService {
	void saveVePayments(String formStore, String gridStore, String caller);
	void updateVePaymentsById(String formStore, String gridStore, String caller);
	void deleteVePayments(int pa_id, String caller);
	void updateVendorBankById(String formStore, String gridStore);
}
