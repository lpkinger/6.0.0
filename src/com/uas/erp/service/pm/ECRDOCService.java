package com.uas.erp.service.pm;

public interface ECRDOCService {
	void saveECRDOC(String formStore, String gridStore, String caller);
	void updateECRDOCById(String formStore, String gridStore, String caller);
	void deleteECRDOC(int ecr_id, String caller);
	void auditECRDOC(int ecr_id, String caller);
	void resAuditECRDOC(int ecr_id, String caller);
	void submitECRDOC(int ecr_id, String caller);
	void resSubmitECRDOC(int ecr_id, String caller);
}
