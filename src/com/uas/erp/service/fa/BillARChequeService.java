package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface BillARChequeService {
	void saveBillARCheque(String formStore, String caller);

	void updateBillARChequeById(String formStore, String caller);

	void deleteBillARCheque(int bar_id, String caller);

	void printBillARCheque(int bar_id, String caller);

	void auditBillARCheque(int bar_id, String caller);

	void resAuditBillARCheque(int bar_id, String caller);

	void submitBillARCheque(int bar_id, String caller);

	void resSubmitBillARCheque(int bar_id, String caller);

	void endBillARCheque(int bar_id, String reason, String caller);

	void resEndBillARCheque(int bar_id, String caller);

	JSONObject copyBillARCheque(int id, String caller);

	void updateInfo(int id, String text, String caller);

	String turnAccountRegister(int bar_id, String accountcode, String caller);
}
