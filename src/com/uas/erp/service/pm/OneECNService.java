package com.uas.erp.service.pm;


public interface OneECNService {
	void saveOneECN(String formStore, String gridStore, String caller);
	void updateOneECNById(String formStore, String gridStore, String caller);
	void deleteOneECN(int ecn_id, String caller);
	void auditOneECN(int ecn_id, String caller);
	void resAuditOneECN(int ecn_id, String caller);
	void submitOneECN(int ecn_id, String caller);
	void resSubmitOneECN(int ecn_id, String caller);
}
