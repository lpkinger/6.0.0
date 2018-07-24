package com.uas.erp.service.scm;

public interface CreditChangeService {

	void saveCreditChange(String formStore, String caller);

	void updateCreditChangeById(String formStore, String caller);

	void auditCreditChange(int cc_id);

	void auditVendCreditChange(int vc_id);

	void auditCustomerCredit(int cuc_id);
}
