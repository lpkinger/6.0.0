package com.uas.erp.service.crm;



public interface ResourceDistrApplyService {
	void audit(int ra_id,String caller);
	void resAudit(int ra_id,String caller);
	void deleteResourceDistrApply(int id,String caller);	
	void saveResourceDistrApply(String formStore, String param,
			 String caller);
	void updateResourceDistrApply(String formStore, String param,
			 String caller);
}
