package com.uas.erp.service.crm;



public interface MarketAnalyseService {
	void saveMarketAnalyse(String formStore,String caller);
	void deleteMarketAnalyse(int ma_id,String caller);
	void updateMarketAnalyse(String formStore,String caller);
	void auditMarketAnalyse(int ma_id,String caller);
	void resAuditMarketAnalyse(int ma_id,String caller);
	void submitMarketAnalyse(int ma_id,String caller);
	void resSubmitMarketAnalyse(int ma_id,String caller);
}
