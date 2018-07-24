package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface BillOutAPService {
	void saveBillOutAP(String formStore, String gridStore, String caller);

	void updateBillOutAPById(String formStore, String gridStore, String caller);

	void deleteBillOutAP(int bi_id, String caller);

	void printBillOutAP(int bi_id, String caller);

	void auditBillOutAP(int bi_id, String caller);

	void resAuditBillOutAP(int bi_id, String caller);

	void submitBillOutAP(int bi_id, String caller);

	void resSubmitBillOutAP(int bi_id, String caller);

	void accountedBillOutAP(int bi_id, String caller);

	void resAccountedBillOutAP(int bi_id, String caller);

	String[] printBillOutAP(int bi_id, String caller, String reportName, String condition);

	String[] printVoucherCodeBillOutAP(int bi_id, String caller, String reportName, String condition);

	void updateTaxcode(String caller, int bi_id, String bi_refno, String bi_remark);

	JSONObject turnPayPlease(int id, String caller);
}
