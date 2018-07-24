package com.uas.erp.service.scm;

public interface ProdInOutApplyService {
	void saveProdInOutApply(String formStore, String gridStore, String caller);
	void updateProdInOutApplyById(String formStore, String gridStore, String caller);
	void deleteProdInOutApply(int pi_id, String caller);
	void auditProdInOutApply(int pi_id, String caller);
	void resAuditProdInOutApply(int pi_id, String caller);
	void submitProdInOutApply(int pi_id, String caller);
	void resSubmitProdInOutApply(int pi_id, String caller);
	String applyTurnProdIO(String caller, String data, String type);
}
