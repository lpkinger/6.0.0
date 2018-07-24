package com.uas.erp.service.fs;

public interface CustQuotaApplyService {
	void saveCustQuotaApply(String formStore, String caller);

	void updateCustQuotaApply(String formStore, String caller);

	void deleteCustQuotaApply(int id, String caller);

	void submitCustQuotaApply(int id, String caller);

	void resSubmitCustQuotaApply(int id, String caller);

	void auditCustQuotaApply(int id, String caller);

	void resAuditCustQuotaApply(int id, String caller);

	void saveHXSurveyBase(String caller, String formStore, String param1, String param2, String param3);

	void saveHXBusinessCondition(String caller, String formStore);

	void saveHXFinancCondition(String caller, String formStore);

}
