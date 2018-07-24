package com.uas.erp.service.pm;

public interface ECRService {
	void saveECR(String formStore, String gridStore, String caller);
	void updateECRById(String formStore, String gridStore, String caller);
	void deleteECR(int ecr_id, String caller);
	void auditECR(int ecr_id, String caller);
	void resAuditECR(int ecr_id, String caller);
	void submitECR(int ecr_id, String caller);
	void resSubmitECR(int ecr_id, String caller);
}
