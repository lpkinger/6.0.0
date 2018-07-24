package com.uas.erp.service.pm;


public interface MakeMaterialOccurService {
	void saveMakeMaterialOccur(String formStore, String caller);
	void updateMakeMaterialOccurById(String formStore, String caller);
	void deleteMakeMaterialOccur(int mm_id, String caller);
	void auditMakeMaterialOccur(int mm_id, String caller);
	void resAuditMakeMaterialOccur(int mm_id, String caller);
	void submitMakeMaterialOccur(int mm_id, String caller);
	void resSubmitMakeMaterialOccur(int mm_id, String caller);
}
