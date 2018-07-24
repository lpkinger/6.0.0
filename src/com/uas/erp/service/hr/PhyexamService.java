package com.uas.erp.service.hr;


public interface PhyexamService {
	
	void savePhyexam(String formStore, String gridStore, String caller);
	
	void updatePhyexamById(String formStore, String gridStore, String caller);
	
	void deletePhyexam(int pu_id, String caller);

}
