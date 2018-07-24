package com.uas.erp.service.hr;


public interface IndinjuryService {
	
	void saveIndinjury(String formStore, String  caller);
	
	void updateIndinjuryById(String formStore, String  caller);
	
	void deleteIndinjury(int or_id, String  caller);
}
