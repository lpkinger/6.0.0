package com.uas.erp.service.hr;

public interface AccidinsurService {
	
	void saveAccidinsur(String formStore, String caller);
	
	void updateAccidinsurById(String formStore, String caller);
	
	void deleteAccidinsur(int or_id, String caller);
}
