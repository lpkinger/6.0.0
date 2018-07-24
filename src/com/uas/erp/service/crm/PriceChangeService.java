package com.uas.erp.service.crm;



public interface PriceChangeService {
	void savePriceChange(String formStore, String gridStore,String caller);
	void updatePriceChangeById(String formStore, String gridStore,String caller);
	void deletePriceChange(int pc_id,String caller);
	void auditPriceChange(int pc_id,String caller);
	void resAuditPriceChange(int pc_id,String caller);
	void submitPriceChange(int pc_id,String caller);
	void resSubmitPriceChange(int pc_id,String caller);
}
