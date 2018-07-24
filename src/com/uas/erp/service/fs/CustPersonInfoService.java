package com.uas.erp.service.fs;

public interface CustPersonInfoService {
	void saveCustPersonInfo(String formStore, String caller);
	void updateCustPersonInfo(String formStore, String caller);
	void deleteCustPersonInfo(int id, String caller);
	void submitCustPersonInfo(int id, String caller);
	void resSubmitCustPersonInfo(int id, String caller);
	void auditCustPersonInfo(int id, String caller);
	void resAuditCustPersonInfo(int id, String caller);
}
