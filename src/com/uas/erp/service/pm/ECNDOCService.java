package com.uas.erp.service.pm;

public interface ECNDOCService {
	void saveECNDOC(String formStore, String gridStore, String caller);
	void updateECNDOCById(String formStore, String gridStore, String caller);
	void deleteECNDOC(int ecn_id, String caller);
	void auditECNDOC(int ecn_id, String caller);
	void resAuditECNDOC(int ecn_id, String caller);
	void submitECNDOC(int ecn_id, String caller);
	void resSubmitECNDOC(int ecn_id, String caller);
}
