package com.uas.erp.service.wisdomPark;


public interface ActivityCenterService {
	
	void deleteActivityType(String caller, int id);
	
	void saveActivity(String caller, String formStore);
	
	void updateActivity(String caller, String formStore);
	
	void deleteActivity(String caller, int id);
	
	void publishActivity(String caller, int id);
	
	void cancelActivity(String caller, int id);
	
	void advanceEndActivity(String caller, int id);

}