package com.uas.erp.service.crm;



public interface CRMCommonAuditService {
	void audit(String caller,int id,String auditerFieldName,String auditdateFieldName);
	void resAudit(String caller,int id,String auditerFieldName,String auditdateFieldName);
	void confirmCommon(String caller, int id, String auditerFieldName);
}
