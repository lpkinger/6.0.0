package com.uas.erp.service.oa;


public interface TiChengService {

	void saveTiCheng(String formStore, String param, String caller);
	void deleteTiCheng(int tc_id, String caller);
	void updateTiChengById(String formStore, String param, String caller);
	void submitTiCheng(int tc_id, String caller);
	void resSubmitTiCheng(int tc_id, String caller);
	void auditTiCheng(int tc_id, String caller);
	void resAuditTiCheng(int tc_id, String caller);
	
}
