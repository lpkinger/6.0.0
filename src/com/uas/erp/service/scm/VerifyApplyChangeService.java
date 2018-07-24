package com.uas.erp.service.scm;

public interface VerifyApplyChangeService {
	void saveVerifyApplyChange(String caller, String formStore, String gridStore);
	void updateVerifyApplyChangeById(String caller, String formStore, String gridStore);
	void deleteVerifyApplyChange(String caller, int vc_id);
	void auditVerifyApplyChange(int vc_id, String caller);
	void submitVerifyApplyChange(String caller, int vc_id);
	void resSubmitVerifyApplyChange(String caller, int vc_id);
}
