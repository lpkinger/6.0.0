package com.uas.erp.service.scm;

public interface MakeQualityYCService {
	void saveMakeQualityYC(String formStore, String caller);
	void updateMakeQualityYCById(String formStore, String caller);
	void deleteMakeQualityYC(int mq_id, String caller);
	void auditMakeQualityYC(int mq_id, String caller);
	void resAuditMakeQualityYC(int mq_id, String caller);
	void submitMakeQualityYC(int mq_id, String caller);
	void resSubmitMakeQualityYC(int mq_id, String caller);
}
