package com.uas.erp.service.scm;

public interface EngineerContrastService {
	void saveEngineerContrast(String formStore, String gridStore, String caller);
	void deleteEngineerContrast(int ec_id, String caller);
	void updateEngineerContrastById(String formStore, String gridStore, String caller);
	void submitEngineerContrast(int ec_id, String caller);
	void resSubmitEngineerContrast(int ec_id, String caller);
	void auditEngineerContrast(int ec_id, String caller);
	void resAuditEngineerContrast(int ec_id, String caller);
}
