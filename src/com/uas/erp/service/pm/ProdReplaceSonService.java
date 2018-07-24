package com.uas.erp.service.pm;


public interface ProdReplaceSonService {
	void saveProdReplaceSon(String formStore, String gridStore, String  caller);
	void updateProdReplaceSonById(String formStore, String gridStore, String  caller);
	void deleteProdReplaceSon(int bo_id, String  caller);
	void auditProdReplaceSon(int bo_id, String  caller);
	void resAuditProdReplaceSon(int bo_id, String  caller);
	void submitProdReplaceSon(int bo_id, String  caller);
	void resSubmitProdReplaceSon(int bo_id, String  caller);
}
