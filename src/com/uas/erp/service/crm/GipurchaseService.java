package com.uas.erp.service.crm;



public interface GipurchaseService {
	void saveGipurchase(String formStore, String gridStore,String caller);
	
	void updateGipurchaseById(String formStore, String gridStore,String caller);
	
	void deleteGipurchase(int gp_id,String caller);
	
	void auditGipurchase(int gp_id,String caller);
	
	void resAuditGipurchase(int gp_id,String caller);
	
	void submitGipurchase(int gp_id,String caller);
	
	void resSubmitGipurchase(int gp_id,String caller);
	
	void turnOaacceptance(String formdata,String griddata,String caller);
}
