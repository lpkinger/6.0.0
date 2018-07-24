package com.uas.erp.service.oa;

public interface SalaryBillService {
	void auditSalaryBill(int sb_id);
	void resAuditSalaryBill(int sb_id);
	int createVoucher(int sb_id);
}
