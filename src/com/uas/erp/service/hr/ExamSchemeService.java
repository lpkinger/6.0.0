package com.uas.erp.service.hr;

public interface ExamSchemeService {
	void saveExamScheme(String formStore, String gridStore, String caller);
	void deleteExamScheme(int es_id, String caller);
	void updateExamSchemeById(String formStore,String gridStore, String caller);
	void submitExamScheme(int es_id, String caller);
	void resSubmitExamScheme(int es_id, String caller);
	void auditExamScheme(int es_id, String caller);
	void resAuditExamScheme(int es_id, String caller);
}
