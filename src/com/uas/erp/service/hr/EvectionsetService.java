package com.uas.erp.service.hr;


public interface EvectionsetService {
	
	void saveEvectionset(String formStore, String  caller);
	
	void updateEvectionsetById(String formStore, String  caller);
	
	void deleteEvectionset(int or_id, String  caller);
}
