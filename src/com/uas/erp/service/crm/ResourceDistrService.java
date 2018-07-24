package com.uas.erp.service.crm;



public interface ResourceDistrService {

	void saveResourceDistr(String formStore, String param, 
			String caller);

	void deleteResourceDistr(int id,String caller);

	void updateResourceDistr(String formStore, String param, 
			String caller);

}
