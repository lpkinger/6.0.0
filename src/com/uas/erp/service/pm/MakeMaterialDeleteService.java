package com.uas.erp.service.pm;


public interface MakeMaterialDeleteService {
	void saveMakeMaterialDelete(String formStore, String caller);
	void updateMakeMaterialDeleteById(String formStore, String caller);
	void deleteMakeMaterialDelete(int mm_id, String caller);
	void auditMakeMaterialDelete(int mm_id, String caller);
	void resAuditMakeMaterialDelete(int mm_id, String caller);
	void submitMakeMaterialDelete(int mm_id, String caller);
	void resSubmitMakeMaterialDelete(int mm_id, String caller);
}
