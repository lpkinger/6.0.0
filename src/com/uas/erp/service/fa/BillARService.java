package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface BillARService {
	void saveBillAR(String formStore, String param, String caller);

	void updateBillARById(String formStore, String param, String caller);

	void deleteBillAR(int bar_id, String caller);

	void printBillAR(int bar_id, String caller);

	void auditBillAR(int bar_id, String caller);

	void resAuditBillAR(int bar_id, String caller);

	void submitBillAR(int bar_id, String caller);

	void resSubmitBillAR(int bar_id, String caller);

	void nullifyBillAR(int bar_id, String caller);

	void changeBank(String language, String caller, String data);

	JSONObject copyBillAR(int id, String caller);

	void updateInfo(int id, String text, String caller);

	void updateBillARSplit(String formStore, String param, String caller);

	void splitDetailBillAR(int brs_id);

	void cancelSplitDetailBillAR(int brs_id);

	void splitBillAR(int bar_id);
}
