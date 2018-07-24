package com.uas.erp.service.hr;


public interface AccountemService {
	void updateAccountById(String formStore,  String gridStore,String caller);

	void copyRelativeSettings(String toobjects, int fromemid, String caller);	
}
