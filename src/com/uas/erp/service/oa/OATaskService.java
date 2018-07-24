package com.uas.erp.service.oa;


public interface OATaskService {
	void saveOATask(String formStore, String gridStore,String gridStore2,String gridStore3,String caller);
	void deleteOATask(int id, String  caller);
	void updateOATask(String formStore,String gridStore,String gridStore2, String gridStore3,String  caller);
}
