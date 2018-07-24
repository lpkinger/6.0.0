package com.uas.erp.service.pm;

public interface MakeNoticeService {
	void saveMakeNotice(String formStore, String gridStore,String caller);
	void updateMakeNoticeById(String formStore,String gridStore, String caller);
	void deleteMakeNotice(int mn_id, String caller);
	void auditMakeNotice(int mn_id, String caller);
	void resAuditMakeNotice(int mn_id, String caller);
	void submitMakeNotice(int mn_id, String caller);
	void resSubmitMakeNotice(int mn_id, String caller);
	int turnMake(int mn_id, String caller);
	int turnOutSource(int mn_id, String caller);
}
