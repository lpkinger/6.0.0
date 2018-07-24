package com.uas.erp.service.crm;



public interface PreResourceService {
	void audit(int pr_id,String caller);
	void resAudit(int pr_id,String caller);
}
