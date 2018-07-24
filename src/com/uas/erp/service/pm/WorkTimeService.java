package com.uas.erp.service.pm;


public interface WorkTimeService {
	void saveWorkTime(String formStore, String  caller);
	void updateWorkTimeById(String formStore, String  caller);
	void deleteWorkTime(int wt_id, String  caller);
}
