package com.uas.erp.service.hr;


public interface AccountTransferService {
	void saveAccountTransfer(String formStore,String  caller);
	void deleteAccountTransfer(int at_id, String  caller);
	void updateAccountTransferById(String formStore,String  caller);
	void submitAccountTransfer(int at_id, String  caller);
	void resSubmitAccountTransfer(int at_id, String  caller);
	void auditAccountTransfer(int at_id, String  caller);
	void resAuditAccountTransfer(int at_id, String  caller);
}
