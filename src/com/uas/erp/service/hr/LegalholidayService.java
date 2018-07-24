package com.uas.erp.service.hr;


public interface LegalholidayService {
	
	void saveLegalholiday(String formStore, String caller);
	
	void updateLegalholidayById(String formStore, String caller);
	
	void deleteLegalholiday(int lh_id, String caller);
}
