package com.uas.erp.service.fs;

public interface CustomerQuotaService {
	void saveCustomerQuota(String formStore, String caller);

	void updateCustomerQuota(String formStore, String caller);

	void deleteCustomerQuota(int id, String caller);

	void submitCustomerQuota(int id, String caller);

	void resSubmitCustomerQuota(int id, String caller);

	void auditCustomerQuota(int id, String caller);

	void resAuditCustomerQuota(int id, String caller);

	void getDefaultDatas(int cqid);

	void saveSurveyBase(String caller, String formStore, String param1, String param2, String param3, String param4);

	void saveCreditStatus(String caller, String formStore, String param1, String param2);

	void saveIncomeProfit(String caller, String formStore, String param1, String param2);

	void saveSurveyConclusion(String caller, String formStore);

	void saveFaReportAnalysis(String caller, String formStore);

	void saveGuarantee(String caller, String formStore);

	void saveMFCustInfo(String gridStore);

	void saveSurveyBaseZL(String caller, String formStore, String param1, String param2, String param3);

}
