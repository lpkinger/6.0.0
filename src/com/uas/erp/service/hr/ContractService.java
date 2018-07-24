package com.uas.erp.service.hr;


public interface ContractService {
	
	void saveContract(String formStore, String  caller);
	
	void updateContractById(String formStore, String  caller);
	
	void deleteContract(int co_id, String  caller);
	
	void auditContract(int co_id, String  caller);
	
	void resAuditContract(int co_id, String  caller);
	
	void submitContract(int co_id, String  caller);
	
	void resSubmitContract(int co_id, String  caller);
}
