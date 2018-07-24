package com.uas.erp.service.oa;


public interface FuelSubsidyService {
	void saveFuelSubsidy(String formStore,String  caller);
	void deleteFuelSubsidy(int fs_id, String  caller);
	void updateFuelSubsidyById(String formStore,String  caller);
	void submitFuelSubsidy(int fs_id, String  caller);
	void resSubmitFuelSubsidy(int fs_id, String  caller);
	void auditFuelSubsidy(int fs_id, String  caller);
	void resAuditFuelSubsidy(int fs_id, String  caller);
	void confirmFuelSubsidy(int fs_id, String caller);
}
