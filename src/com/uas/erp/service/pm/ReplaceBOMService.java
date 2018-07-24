package com.uas.erp.service.pm;


public interface ReplaceBOMService {
	void saveReplaceBOM(String formStore, String gridStore, String  caller);
	void updateReplaceBOMById(String formStore, String gridStore, String  caller);
	void deleteReplaceBOM(int bd_id, String  caller);
}
