package com.uas.erp.service.pm;

public interface StepService {
	void saveStep(String formStore, String caller);

	void updateStepById(String formStore, String caller);

	void deleteStep(int st_id, String caller);

	void auditStep(int st_id, String caller);

	void resAuditStep(int st_id, String caller);

	void submitStep(int st_id, String caller);

	void resSubmitStep(int st_id, String caller);
}
