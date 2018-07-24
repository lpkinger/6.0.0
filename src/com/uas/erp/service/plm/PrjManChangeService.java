package com.uas.erp.service.plm;


public interface PrjManChangeService {
	void savePrjManChange(String formStore, String gridStore,String caller);
	void deletePrjManChange(int mc_id,String caller);
	void updatePrjManChangeById(String formStore,String gridStore,String caller);
	void submitPrjManChange(int mc_id,String caller);
	void resSubmitPrjManChange(int mc_id,String caller);
	void auditPrjManChange(int mc_id,String caller);
	void resAuditPrjManChange(int mc_id,String caller);
}
