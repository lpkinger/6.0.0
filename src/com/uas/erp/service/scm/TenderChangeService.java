package com.uas.erp.service.scm;

public interface TenderChangeService {
	
	void saveTenderChange(String formStore, String caller);

	void updateTenderChangeById(String formStore, String caller);

	void deleteTenderChange(int tc_id, String caller);

	void auditTenderChange(int tc_id, String caller);

	void submitTenderChange(int tc_id, String caller);

	void resSubmitTenderChange(int tc_id, String caller);
}
