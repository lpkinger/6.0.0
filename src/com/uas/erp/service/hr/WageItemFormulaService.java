package com.uas.erp.service.hr;


public interface WageItemFormulaService {
	
	void saveWageItemFormula(String formStore, String  caller);
	
	void updateWageItemFormulaById(String formStore, String  caller);
	
	void deleteWageItemFormula(int wif_id, String  caller);
}
