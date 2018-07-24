package com.uas.erp.service.oa;


public interface PowerApplyService {
	void savePowerApply(String formStore, String caller);
	void updatePowerApply(String formStore, String caller);
	void deletePowerApply(int  id, String caller);
	void auditPowerApply(int  id, String caller);
	void resAuditPowerApply(int  id, String caller);
	void submitPowerApply(int  id, String caller);
	void resSubmitPowerApply(int  id, String caller);
}
