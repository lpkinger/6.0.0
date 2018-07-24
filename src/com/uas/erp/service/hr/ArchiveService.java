package com.uas.erp.service.hr;


public interface ArchiveService {
	
	void saveArchive(String formStore, String[] gridStore, String  caller);
	
	void updateArchiveById(String formStore, String[] gridStore, String  caller);
	
	void deleteArchive(int ar_id, String  caller);
	
}
