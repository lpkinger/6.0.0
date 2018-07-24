package com.uas.erp.service.fa;

public interface AccountRegisterService {
	void saveAccountRegister(String caller, String formStore, String gridStore);

	void updateAccountRegisterById(String caller, String formStore,
			String gridStore);

	void deleteAccountRegister(String caller, int ar_id);

	void printAccountRegister(String caller, int ar_id);

	void auditAccountRegister(String caller, int ar_id);

	void resAuditAccountRegister(String caller, int ar_id);

	void submitAccountRegister(String caller, int ar_id);

	void resSubmitAccountRegister(String caller, int ar_id);
}
