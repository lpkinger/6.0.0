package com.uas.erp.service.crm;



public interface ScheduleService {
	void saveSchedule(String formStore,String caller);
	void deleteSchedule(int sc_id,String caller);
	void updateSchedule(String formStore,String caller);
}
