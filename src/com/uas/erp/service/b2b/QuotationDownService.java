package com.uas.erp.service.b2b;

public interface QuotationDownService {

	void updateQuotationDown(String formStore, String param, String caller);

	void submitQuotationDowny(int id, String caller);

	void resSubmitQuotationDown(int id, String caller);

	void auditQuotationDown(int id, String caller);

	void resAuditQuotationDown(int id, String caller);
	
	void deleteQuotationDownDetail(int id);

}
