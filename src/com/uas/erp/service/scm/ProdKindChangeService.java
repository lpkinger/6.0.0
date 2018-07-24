package com.uas.erp.service.scm;


public interface ProdKindChangeService {
	void saveProdKindChange(String formStore, String gridStore, String caller);

	void updateProdKindChangeById(String formStore, String gridStore, String caller);

	void deleteProdKindChange(int pc_id, String caller);

	void printProdKindChange(int pc_id, String caller);

	void auditProdKindChange(int pc_id, String caller);

	void resAuditProdKindChange(int pc_id, String caller);

	void submitProdKindChange(int pc_id, String caller);

	void resSubmitProdKindChange(int pc_id, String caller);
}
