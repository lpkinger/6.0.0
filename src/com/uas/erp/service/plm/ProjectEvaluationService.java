package com.uas.erp.service.plm;

public interface ProjectEvaluationService {
	void saveProjectEvaluation(String formStore,String caller);
	void deleteProjectEvaluation(int pe_id, String caller);
	void updateProjectEvaluation(String formStore, String caller);
	void submitProjectEvaluation(int pe_id, String caller);
	void auditProjectEvaluation(int pe_id, String caller);
	void resSubmitProjectEvaluation(int id, String caller);
	void resAuditProjectEvaluation(int id, String caller);
	int turnProject(int pe_id, String caller);
	void turn(int pe_id,String type,String caller);
}
