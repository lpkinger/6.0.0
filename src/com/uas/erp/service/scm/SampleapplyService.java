package com.uas.erp.service.scm;

public interface SampleapplyService {

	void saveSampleapply(String formStore, String gridstore, String caller);

	void updateSampleapplyById(String formStore, String gridstore, String caller);

	void deleteSampleapply(int sa_id, String caller);

	void auditSampleapply(int sa_id, String caller);

	void resAuditSampleapply(int sa_id, String caller);

	void submitSampleapply(int sa_id, String caller);

	void resSubmitSampleapply(int sa_id, String caller);

	String turnProductSample(String caller, String data);
	
	String turnProductApproval(String data);
}
