package com.uas.erp.service.oa;


public interface StoragesetService {
	
	void saveStorageset(String formStore, String  caller);
	
	void updateStorageset(String formStore, String  caller);
	
	void deleteStorageset(int ss_id, String  caller);
}
