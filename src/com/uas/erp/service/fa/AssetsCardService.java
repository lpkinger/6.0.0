package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface AssetsCardService {
	void saveAssetsCard(String formStore, String gridStore, String caller);

	void updateAssetsCardById(String formStore, String gridStore, String caller);

	void deleteAssetsCard(int ac_id, String caller);

	void auditAssetsCard(int ac_id, String caller);

	void resAuditAssetsCard(int ac_id, String caller);

	void submitAssetsCard(int ac_id, String caller);

	void resSubmitAssetsCard(int ac_id, String caller);

	JSONObject copyAssetsCard(int ac_id, String accode, int kindid, String caller);

	void updateusestatus(int ac_id, String usestatus);

	JSONObject getAssetsCardCodeNum(String caller, Object kind);
}
