package com.uas.erp.service.crm;



public interface PrPreResourceService {

	void audit(int id,String caller);

	void resAudit(int id,String caller);

	int turnPreResource(int id,String caller);

}
