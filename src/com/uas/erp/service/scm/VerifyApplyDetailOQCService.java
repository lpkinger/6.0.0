package com.uas.erp.service.scm;

public interface VerifyApplyDetailOQCService {
	void saveVerifyApplyDetailOQC(String formStore, String caller);
	void updateVerifyApplyDetailOQCById(String formStore, String caller);
	void deleteVerifyApplyDetailOQC(int ve_id, String caller);
	void auditVerifyApplyDetailOQC(int ve_id, String caller);
	void resAuditVerifyApplyDetailOQC(int ve_id, String caller);
	void submitVerifyApplyDetailOQC(int ve_id, String caller);
	void resSubmitVerifyApplyDetailOQC(int ve_id, String caller);
	void updatePMC(Integer id, String pmc, String caller);
}
