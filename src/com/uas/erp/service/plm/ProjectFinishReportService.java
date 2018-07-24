package com.uas.erp.service.plm;

public interface ProjectFinishReportService {
	void saveProjectFinishReport(String formStore, String caller);

	void deleteProjectFinishReport(int id, String caller);

	void updateProjectFinishReport(String formStore, String caller);

	void auditProjectFinishReport(int id, String caller);

	void submitProjectFinishReport(int id, String caller);

	void resSubmitProjectFinishReport(int id, String caller);

	void resAuditProjectFinishReport(int id, String caller);
	
	String turnCapitalization(String caller, String data);
}
