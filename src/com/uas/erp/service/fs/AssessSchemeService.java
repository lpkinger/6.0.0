package com.uas.erp.service.fs;


public interface AssessSchemeService {
	void saveAssessScheme(String formStore,String param,String caller);
	void updateAssessScheme(String formStore,String param,String caller);
	void deleteAssessScheme(int id, String caller);
	void submitAssessScheme(int id, String caller);
	void resSubmitAssessScheme(int id, String caller);
	void auditAssessScheme(int id, String caller);
	void resAuditAssessScheme(int id, String caller);
}
