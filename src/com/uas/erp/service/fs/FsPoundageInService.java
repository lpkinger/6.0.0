package com.uas.erp.service.fs;

import net.sf.json.JSONObject;

public interface FsPoundageInService {
	void saveFsPoundageIn(String formStore, String caller);

	void updateFsPoundageIn(String formStore, String caller);

	void deleteFsPoundageIn(int id, String caller);

	void submitFsPoundageIn(int id, String caller);

	void resSubmitFsPoundageIn(int id, String caller);

	void auditFsPoundageIn(int id, String caller);

	void resAuditFsPoundageIn(int id, String caller);

	public JSONObject turnBankRegister(String caller, int pi_id);
}
