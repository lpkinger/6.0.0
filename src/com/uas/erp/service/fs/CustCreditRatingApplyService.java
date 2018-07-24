package com.uas.erp.service.fs;

public interface CustCreditRatingApplyService {
	void saveCustCreditRatingApply(String formStore, String caller);

	void updateCustCreditRatingApply(String formStore, String caller);

	void deleteCustCreditRatingApply(int id, String caller);

	void submitCustCreditRatingApply(int id, String caller);

	void resSubmitCustCreditRatingApply(int id, String caller);

	void auditCustCreditRatingApply(int id, String caller);

	void resAuditCustCreditRatingApply(int id, String caller);

	String getDisplay(String caller, Integer craid, String type);

	void saveCustCreditTargets(String datas);

	void MeasureScore(int craid, String type);
}
