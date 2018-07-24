package com.uas.erp.service.fa;



public interface PaymentsDetailArpService {
	void savePayments(String caller ,String formStore, String gridStore);
	void updatePaymentsById(String caller, String formStore, String gridStore);
	void deletePayments(String caller, int pa_id);
	void auditPayments(String caller, int pa_id);
	void resAuditPayments(String caller, int pa_id);
	void submitPayments(String caller, int pa_id);
	void resSubmitPayments(String caller, int pa_id);
	void bannedPayments(String caller, int pa_id);
	void resBannedPayments(String caller, int pa_id);
}
