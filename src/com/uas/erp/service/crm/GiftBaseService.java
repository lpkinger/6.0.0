package com.uas.erp.service.crm;



public interface GiftBaseService {
	void saveGiftBase(String formStore,String caller);
	
	void updateGiftBase(String formStore,String caller);
	
	void deleteGiftBase(int gi_id,String caller);
}
