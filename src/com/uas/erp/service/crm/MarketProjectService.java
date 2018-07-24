package com.uas.erp.service.crm;



public interface MarketProjectService {
	void saveMarketProject(String formStore,String gridStore,String caller);
	void deleteMarketProject(int prjplan_id,String caller);
	void updateMarketProject(String formStore,String gridStore,String caller);
}
