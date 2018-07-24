package com.uas.erp.service.oa;


public interface AgencyService {
	
	void save(String formStore, String caller);

	void updateAgency(String formStore, String caller);

	void deleteAgency(int id, String caller);

}
