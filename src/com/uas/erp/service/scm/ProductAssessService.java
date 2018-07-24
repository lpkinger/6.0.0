package com.uas.erp.service.scm;

public interface ProductAssessService {
	void saveProductAssess(String formStore, String caller);
	void deleteProductAssess(int pa_id, String caller);
	void updateProductAssess(String formStore, String caller);
	void submitProductAssess(int pa_id, String caller);
	void resSubmitProductAssess(int pa_id, String caller);
	void auditProductAssess(int pa_id, String caller);
	void resAuditProductAssess(int pa_id, String caller);
	void turnProductApplication(int pa_id, String caller);
}
