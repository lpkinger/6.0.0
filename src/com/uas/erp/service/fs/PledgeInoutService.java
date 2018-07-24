package com.uas.erp.service.fs;

public interface PledgeInoutService {
	void savePledgeInout(String formStore, String  caller);
	void updatePledgeInout(String formStore, String  caller);
	void deletePledgeInout(int fl_id, String  caller);
	void submitPledgeInout(int fl_id, String caller);
	void resSubmitPledgeInout(int fl_id, String caller);
	void auditPledgeInout(int fl_id, String caller);
	void resAuditPledgeInout(int fl_id, String caller);
}
