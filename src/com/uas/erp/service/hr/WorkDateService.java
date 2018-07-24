package com.uas.erp.service.hr;


public interface WorkDateService {
	
	void saveWorkDate(String formStore, String  caller);
	
	void updateWorkDateById(String formStore, String  caller);
	
	void deleteWorkDate(int wd_id, String  caller);

    public void setEmpWorkDate(int wdid, String condition, String caller);
}
