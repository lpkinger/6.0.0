package com.uas.erp.service.hr;


public interface CCSQChangeService {
	void saveCCSQChange(String formStore, String gridStore, String  caller);
	void deleteCCSQChange(int cc_id, String  caller);
	void updateCCSQChangeById(String formStore,String gridStore, String  caller);
	void submitCCSQChange(int cc_id, String  caller);
	void resSubmitCCSQChange(int cc_id, String  caller);
	void auditCCSQChange(int cc_id, String  caller);
	void resAuditCCSQChange(int cc_id, String  caller);
}
