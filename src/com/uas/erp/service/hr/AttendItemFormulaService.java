package com.uas.erp.service.hr;


public interface AttendItemFormulaService {
	
	void saveAttendItemFormula(String formStore, String  caller);
	
	void updateAttendItemFormulaById(String formStore, String  caller);
	
	void deleteAttendItemFormula(int aif_id, String  caller);
}
