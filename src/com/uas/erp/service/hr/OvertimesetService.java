package com.uas.erp.service.hr;


public interface OvertimesetService {
	
	void saveOvertimeset(String formStore, String  caller);
	
	void updateOvertimesetById(String formStore, String  caller);
	
	void deleteOvertimeset(int or_id, String  caller);
}
