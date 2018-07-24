package com.uas.erp.service.scm;


public interface ProdChangeService {
	void saveProdChange(String formStore, String gridStore, String caller);

	void updateProdChangeById(String formStore, String gridStore, String caller);

	void deleteProdChange(int pc_id, String caller);

	void printProdChange(int pc_id, String caller);

	void auditProdChange(int pc_id, String caller);

	void resAuditProdChange(int pc_id, String caller);

	void submitProdChange(int pc_id, String caller);

	void resSubmitProdChange(int pc_id, String caller);
}
