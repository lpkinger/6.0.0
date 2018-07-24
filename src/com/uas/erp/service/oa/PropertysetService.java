package com.uas.erp.service.oa;


public interface PropertysetService {
	
	void savePropertyset(String formStore, String  caller);
	
	void updatePropertyset(String formStore, String  caller);
	
	void deletePropertyset(int ps_id, String  caller);
}
