package com.uas.erp.service.pm;


public interface MakeSonService {
	void saveMakeSon(String formStore, String caller);
	void updateMakeSonById(String formStore, String caller);
	void deleteMakeSon(int ma_id, String caller);
	void auditMakeSon(int ma_id, String caller);
	void resAuditMakeSon(int ma_id, String caller);
	void submitMakeSon(int ma_id, String caller);
	void resSubmitMakeSon(int ma_id, String caller);
}
