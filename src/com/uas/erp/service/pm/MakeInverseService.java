package com.uas.erp.service.pm;

public interface MakeInverseService {
	void saveMakeInverse(String formStore, String caller);
	void updateMakeInverseById(String formStore, String caller);
	void deleteMakeInverse(int ma_id, String caller);
	void auditMakeInverse(int ma_id, String caller);
	void resAuditMakeInverse(int ma_id, String caller);
	void submitMakeInverse(int ma_id, String caller);
	void resSubmitMakeInverse(int ma_id, String caller);
}
