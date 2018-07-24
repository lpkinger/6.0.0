package com.uas.erp.service.b2b;

public interface CustSendSampleService {

	void updateCustSendSample(String formStore, String caller);

	void submitCustSendSample(int id, String caller);

	void resSubmitCustSendSample(int id, String caller);

	void auditCustSendSample(int id, String caller);

	void resAuditCustSendSample(int id, String caller);

	int CustSendToProdInout(String formStore, String param, String caller);

	int CustSendToPurInout(String formStore, String param, String caller);

}
