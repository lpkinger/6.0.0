package com.uas.erp.service.scm;

public interface WorkCenterService {
	void saveWorkCenter(String formStore, String gridStore, String caller);
	void updateWorkCenterById(String formStore, String gridStore, String caller);
	void deleteWorkCenter(int wc_id, String caller);
}
