package com.uas.erp.service.oa;


public interface CustomFlowService {
	void saveCustomFlow(String formStore,String gridStore, String  caller);
	void updateCustomFlowById(String formStore,String gridStore, String  caller);
	void deleteCustomFlow(int cf_id, String  caller);
	

}
