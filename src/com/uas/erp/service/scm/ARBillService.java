package com.uas.erp.service.scm;

public interface ARBillService {
	void saveARBill(String formStore, String gridStore, String caller);
	void updateARBillById(String formStore, String gridStore, String caller);
	void deleteARBill(int ab_id, String caller);
	void printARBill(int ab_id, String caller);
	void auditARBill(int ab_id, String caller);
	void resAuditARBill(int ab_id, String caller);
	void submitARBill(int ab_id, String caller);
	void resSubmitARBill(int ab_id, String caller);
}
