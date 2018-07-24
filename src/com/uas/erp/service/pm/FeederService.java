package com.uas.erp.service.pm;

public interface FeederService {
	void saveFeeder(String formStore, String caller);

	void updateFeederById(String formStore, String caller);

	void deleteFeeder(int fe_id, String caller);

	void auditFeeder(int fe_id, String caller);

	void resAuditFeeder(int fe_id, String caller);

	void submitFeeder(int fe_id, String caller);

	void resSubmitFeeder(int fe_id, String caller);

	void saveFeederRepairLog(String caller, int fe_id, String remark,
			boolean ifclear);

	void saveFeederScrapLog(String caller, int fe_id, String remark);

	void vastTurnMaintain(String caller, String data);
}
