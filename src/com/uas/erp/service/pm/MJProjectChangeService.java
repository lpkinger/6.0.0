package com.uas.erp.service.pm;

public interface MJProjectChangeService {

	void auditMJProjectChange(int wsc_id, String caller);

	void auditMJProject(int ws_id, String caller);

	void resAuditMJProject(int ws_id, String caller);

}
