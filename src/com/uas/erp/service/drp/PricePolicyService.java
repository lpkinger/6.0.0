package com.uas.erp.service.drp;



public interface PricePolicyService {
	
	void savePricePolicy(String formStore,  String caller);
	
	void updatePricePolicyById(String formStore,  String caller);
	
	void deletePricePolicy(int pp_id,  String caller);
	
	void auditPricePolicy(int pp_id,  String caller);
	
	void resAuditPricePolicy(int pp_id,  String caller);
	
	void submitPricePolicy(int pp_id,  String caller);
	
	void resSubmitPricePolicy(int pp_id,  String caller);
	
}
