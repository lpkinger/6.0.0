package com.uas.erp.service.oa;


public interface OaapplianceService {
	
	void saveOaappliance(String formStore, String  caller);
	
	void updateOaappliance(String formStore, String  caller);
	
	void deleteOaappliance(int bd_id, String  caller);
}
