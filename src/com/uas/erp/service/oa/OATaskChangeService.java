package com.uas.erp.service.oa;


public interface OATaskChangeService {
	void auditOATaskChange(int ptc_id,String  caller);
	void resAuditOATaskChange(int ptc_id,String  caller);
}
