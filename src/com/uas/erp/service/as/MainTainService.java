package com.uas.erp.service.as;

public interface MainTainService {
	void saveMainTain(String formStore, String gridStore, String caller);
	void deleteMainTain(int ct_id, String caller);
	void updateMainTain(String formStore, String gridStore,
			String caller);
	void submitMainTain(int ct_id, String caller);
	void resSubmitMainTain(int ct_id, String caller);
	void auditMainTain(int ct_id, String caller);
	void resAuditMainTain(int ct_id, String caller);
	void marketMainTain(int id, String value,String caller);
}
