package com.uas.erp.service.crm;



public interface StageChangeService {
	void audit(int sc_id,String caller);
	void resAudit(int sc_id,String caller);
}
