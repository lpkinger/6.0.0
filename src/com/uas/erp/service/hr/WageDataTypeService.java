package com.uas.erp.service.hr;


public interface WageDataTypeService {
	
	void saveWageDataType(String formStore, String  caller);
	
	void updateWageDataTypeById(String formStore, String  caller);
	
	void deleteWageDataType(int wdt_id, String  caller);
}
