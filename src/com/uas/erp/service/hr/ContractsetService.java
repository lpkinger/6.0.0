package com.uas.erp.service.hr;


public interface ContractsetService {
	
	void saveContractset(String formStore, String  caller);
	
	void updateContractsetById(String formStore, String  caller);
	
	void deleteContractset(int or_id, String  caller);
}
