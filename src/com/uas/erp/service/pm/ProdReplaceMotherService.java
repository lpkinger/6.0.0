package com.uas.erp.service.pm;


public interface ProdReplaceMotherService {
	void saveProdReplaceMother(String formStore, String gridStore, String caller);
	void updateProdReplaceMotherById(String formStore, String gridStore, String caller);
	void deleteProdReplaceMother(int pr_id, String caller);
	void auditProdReplaceMother(int pr_id, String caller);
	void resAuditProdReplaceMother(int pr_id, String caller);
	void submitProdReplaceMother(int pr_id, String caller);
	void resSubmitProdReplaceMother(int pr_id, String caller);
}
