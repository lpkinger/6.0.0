package com.uas.erp.service.scm;

public interface EvaluationService {
	void saveEvaluation(String formStore, String gridStore, String gridStore2, String gridStore3);

	void updateEvaluationById(String formStore, String gridStore, String gridStore2, String gridStore3);

	void deleteEvaluation(int ev_id);

	void deleteEvaluationDetail(String evd_id, String caller);

	void auditEvaluation(int ev_id, String caller);

	void resAuditEvaluation(int ev_id);

	void submitEvaluation(int ev_id);

	void resSubmitEvaluation(int ev_id);

	void bannedEvaluation(int ev_id);

	void resBannedEvaluation(int ev_id);

	String[] printEvaluation(int ev_id, String reportName, String condition);

	void calBOMOfferCost(int ev_id, int bo_id, String pr_code);

	/**
	 * 成本计算
	 * 
	 * @param ev_id
	 * @param bo_id
	 * @param pr_code
	 */
	void calBOMCost(int ev_id, int bo_id, String pr_code);

	void bomInsert(int ev_id);

	void bomVastCost(int ev_id);

	int turnQuotation(int ev_id);

	void clearBomOffer(int ev_id);
}
