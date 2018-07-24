package com.uas.erp.service.drp;



public interface PromotionService {
	
	void savePromotion(String formStore, String gridStore,  String caller);
	
	void updatePromotionById(String formStore, String gridStore,  String caller);
	
	void deletePromotion(int pi_id,  String caller);
	
	void auditPromotion(int pi_id,  String caller);
	
	void resAuditPromotion(int pi_id,  String caller);
	
	void submitPromotion(int pi_id,  String caller);
	
	void resSubmitPromotion(int pi_id,  String caller);
	
}
