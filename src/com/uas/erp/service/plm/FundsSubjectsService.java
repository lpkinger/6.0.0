package com.uas.erp.service.plm;

public interface FundsSubjectsService {
	void saveFundSubject(String formStore);

	void updateFundSubject(String formStore);

	void deleteFundSubject(int id);

	void submitFundSubject(int id);

	void resSubmitFundSubject(int id);

	void auditFundSubject(int id);

	void resAuditFundSubject(int id);
}
