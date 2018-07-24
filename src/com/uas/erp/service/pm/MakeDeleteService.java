package com.uas.erp.service.pm;


public interface MakeDeleteService {
	void saveMakeDelete(String formStore, String caller);
	void updateMakeDeleteById(String formStore, String caller);
	void deleteMakeDelete(int ma_id, String caller);
	void auditMakeDelete(int ma_id, String caller);
	void resAuditMakeDelete(int ma_id, String caller);
	void submitMakeDelete(int ma_id, String caller);
	void resSubmitMakeDelete(int ma_id, String caller);
}
