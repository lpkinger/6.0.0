package com.uas.erp.service.oa;


public interface PropertService {
	
	void savePropert(String formStore, String  caller);
	
	void updatePropert(String formStore, String  caller);
	
	void deletePropert(int ps_id, String  caller);
}
