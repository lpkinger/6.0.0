package com.uas.erp.service.crm;



public interface MarketTaskService {
	void saveMarketTask(String formStore,String caller);
	void deleteMarketTask(int mt_id,String caller);
	void updateMarketTask(String formStore,String caller);
}
