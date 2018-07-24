package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface BillAPChequeService {
	void saveBillAPCheque(String formStore, String caller);

	void updateBillAPChequeById(String formStore, String caller);

	void deleteBillAPCheque(int bar_id, String caller);

	void printBillAPCheque(int bar_id, String caller);

	void auditBillAPCheque(int bar_id, String caller);

	void resAuditBillAPCheque(int bar_id, String caller);

	void submitBillAPCheque(int bar_id, String caller);

	void resSubmitBillAPCheque(int bar_id, String caller);

	void endBillAPCheque(int bar_id, String reason, String caller);

	void resEndBillAPCheque(int bar_id, String caller);

	JSONObject copyBillAPCheque(int id, String caller);

	void updateInfo(int id, String text, String caller);

	String turnAccountRegister(int bar_id, String accountcode, String caller);
}
