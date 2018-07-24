package com.uas.erp.service.fs;

import net.sf.json.JSONObject;

public interface AccountApplyService {

	void saveAccountApply(String formStore, String param1, String param2, String param3, String param4, String caller);

	void updateAccountApply(String formStore, String param1, String param2, String param3, String param4, String caller);

	void deleteAccountApply(int id, String caller);

	void submitAccountApply(int id, String caller);

	void resSubmitAccountApply(int id, String caller);

	void auditAccountApply(int id, String caller);

	void resAuditAccountApply(int id, String caller);

	JSONObject turnBankRegister(String caller, int ar_id);

	void sendReimbursePlan() throws Exception;

	void deleteFsOverdue(int id, String caller);

}
