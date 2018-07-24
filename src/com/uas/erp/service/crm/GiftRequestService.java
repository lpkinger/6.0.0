package com.uas.erp.service.crm;



public interface GiftRequestService {
	void saveGiftRequest(String formStore, String gridStore,String caller);
	
	void updateGiftRequestById(String formStore, String gridStore,String caller);
	
	void deleteGiftRequest(int gr_id,String caller);
	
	void auditGiftRequest(int gr_id,String caller);
	
	void resAuditGiftRequest(int gr_id,String caller);
	
	void submitGiftRequest(int gr_id,String caller);
	
	void resSubmitGiftRequest(int gr_id,String caller);
	
	void turnOaPurchase(String formdata,String griddata,String caller);
}
