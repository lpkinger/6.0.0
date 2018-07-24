package com.uas.erp.service.crm;



public interface SchedulerService {
	void saveScheduler(String formStore,String caller);
	void deleteScheduler(int sc_id,String caller);
	void updateScheduler(String formStore,String caller);
}
