package com.uas.erp.service.hr;


public interface PositiondutyService {
	void savePositionduty(String formStore,String gridStore, String caller);
	void deletePositionduty(int pd_id, String caller);
	void updatePositionduty(String formStore,String gridStore, String caller);
}
