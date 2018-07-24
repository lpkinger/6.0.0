package com.uas.erp.service.fa;

import java.util.Map;



public interface BillOutService {
	void saveBillOut(String formStore, String gridStore, String caller);
	void updateBillOutById(String formStore, String gridStore, String caller);
	void deleteBillOut(int bi_id, String caller);
	//void printBillOut(int bi_id, String caller);
	String[] printBillOut(int bi_id, String reportName, String condition, String caller);
	void auditBillOut(int bi_id, String caller);
	void resAuditBillOut(int bi_id, String caller);
	void submitBillOut(int bi_id, String caller);
	void resSubmitBillOut(int bi_id, String caller);
	void accountedBillOut(int bi_id, String caller);
	void resAccountedBillOut(int bi_id, String caller);
	String[] printVoucherCodeBillOut(int bi_id, String caller, String reportName,
			String condition);
	void updateTaxcode(String caller, int bi_id, String bi_refno, String bi_remark);
	Map<String, Object> openInvoice(String caller, int bi_id);
	String cancelInvoiceApply(String caller, int bi_id);
	String queryInvoiceInfo(String caller, int bi_id);
	String getTaxWebSite();
}
