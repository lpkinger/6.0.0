package com.uas.erp.service.hr;


public interface HandCardService {
	
	void saveHandCard(String formStore, String gridStore, String caller);

	void updateHandCardById(String formStore, String gridStore,
			String  caller);

	void deleteHandCard(int hc_id, String  caller);

	void auditHandCard(int hc_id, String  caller);
	
	void insertEmployee(int hcid, String deptcode, String  caller);

}
