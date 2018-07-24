package com.uas.erp.service.pm;

public interface StepReturnService {
	void saveStepReturn(String formStore, String caller);

	void updateStepReturnById(String formStore, String caller);

	void deleteStepReturn(int sr_id, String caller);

	void auditStepReturn(int sr_id, String caller);

	void resAuditStepReturn(int sr_id, String caller);

	void submitStepReturn(int sr_id, String caller);

	void resSubmitStepReturn(int sr_id, String caller);
}
