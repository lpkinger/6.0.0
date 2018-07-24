package com.uas.erp.service.fs;

public interface CustomerInforService {
	void saveCustomerInfor(String formStore, String caller, String param1, String param2, String param3, String param4, String param5);

	void updateCustomerInfor(String formStore, String caller, String param1, String param2, String param3, String param4, String param5);

	void deleteCustomerInfor(int id, String caller);

	void submitCustomerInfor(int id, String caller);

	void resSubmitCustomerInfor(int id, String caller);

	void auditCustomerInfor(int id, String caller);

	void resAuditCustomerInfor(int id, String caller);

	void bannedCustomerInfor(int id, String caller);

	void resBannedCustomerInfor(int id, String caller);
}
