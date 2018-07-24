package com.uas.erp.service.plm;

public interface RequestChangeService {
	void saveRequestChange(String formStore, String caller);
	void updateRequestChangeById(String formStore, String caller);
	void deleteRequestChange(int id, String caller);
	void auditRequestChange(int id, String caller);
	void submitRequestChange(int id, String caller);
	void resSubmitRequestChange(int id, String caller);
	void resAuditRequestChange(int id, String caller);
}
