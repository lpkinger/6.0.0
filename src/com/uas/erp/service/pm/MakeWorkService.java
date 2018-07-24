package com.uas.erp.service.pm;

public interface MakeWorkService {
	void saveMakeWork(String formStore, String caller);
	void updateMakeWorkById(String formStore, String caller);
	void deleteMakeWork(int ma_id, String caller);
	void auditMakeWork(int ma_id, String caller);
	void resAuditMakeWork(int ma_id, String caller);
	void submitMakeWork(int ma_id, String caller);
	void resSubmitMakeWork(int ma_id, String caller);
}
