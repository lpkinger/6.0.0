package com.uas.erp.service.fa;



public interface WageitemService {
	void saveWageItem(String formStore, String caller);
	void updateWageItemById(String formStore, String caller);
	void deleteWageItem(int wg_id, String caller);
	void auditWageItem(int wg_id, String caller);
	void resAuditWageItem(int wg_id, String caller);
	void submitWageItem(int wg_id, String caller);
	void resSubmitWageItem(int wg_id, String caller);
}
