package com.uas.erp.service.hr;


public interface InsuranceService {
	
	void saveInsurance(String formStore, String  caller);
	
	void updateInsuranceById(String formStore, String  caller);
	
	void deleteInsurance(int or_id, String  caller);
}
