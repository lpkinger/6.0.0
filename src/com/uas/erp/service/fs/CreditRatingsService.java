package com.uas.erp.service.fs;

public interface CreditRatingsService {
	void saveCreditRatings(String formStore, String caller);
	void updateCreditRatings(String formStore, String caller);
	void deleteCreditRatings(int id, String caller);
	void submitCreditRatings(int id, String caller);
	void resSubmitCreditRatings(int id, String caller);
	void auditCreditRatings(int id, String caller);
	void resAuditCreditRatings(int id, String caller);
}
