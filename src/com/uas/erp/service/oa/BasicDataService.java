package com.uas.erp.service.oa;


public interface BasicDataService {
	void saveBasicData(String formStore, String caller);
	void updateBasicData(String formStore, String caller);
	void deleteBasicData(int bd_id, String caller);
}
