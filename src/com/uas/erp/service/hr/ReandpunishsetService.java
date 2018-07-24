package com.uas.erp.service.hr;


public interface ReandpunishsetService {
	
	void saveReandpunishset(String formStore, String caller);
	
	void updateReandpunishsetById(String formStore, String caller);
	
	void deleteReandpunishset(int or_id, String caller);
}
