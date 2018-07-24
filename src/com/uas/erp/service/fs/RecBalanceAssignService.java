package com.uas.erp.service.fs;

public interface RecBalanceAssignService {
	
	void updateRecBalanceAssign(String caller, String formStore, String param);

	void deleteRecBalanceAssign(int id, String caller);

	void submitRecBalanceAssign(int id, String caller);

	void resSubmitRecBalanceAssign(int id, String caller);

	void auditRecBalanceAssign(int id, String caller);

	void assignRecBalance(int id, String caller);

	String turnRecBalanceAssign(String data, String caller);
}
