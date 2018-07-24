package com.uas.erp.service.hr;


public interface VacationsetService {
	
	void saveVacationset(String formStore, String  caller);
	
	void updateVacationsetById(String formStore, String  caller);
	
	void deleteVacationset(int or_id, String  caller);
}
