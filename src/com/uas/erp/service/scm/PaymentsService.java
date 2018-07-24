package com.uas.erp.service.scm;

public interface PaymentsService {
	void savePayments(String formStore, String gridStore, String caller);
	void updatePaymentsById(String formStore, String gridStore, String caller);
	void deletePayments(int pa_id, String caller);
	void auditPayments(int pa_id, String caller);
	void resAuditPayments(int pa_id, String caller);
	void submitPayments(int pa_id, String caller);
	void resSubmitPayments(int pa_id, String caller);
	void bannedPayments(int pa_id, String caller);
	void resBannedPayments(int pa_id, String caller);
}
