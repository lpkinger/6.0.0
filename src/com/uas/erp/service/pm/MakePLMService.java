package com.uas.erp.service.pm;


public interface MakePLMService {
	void saveMakeBase(String formStore,String gridStore ,String caller);
	void updateMakeBaseById(String formStore, String gridStore ,String caller);
	void deleteMakeBase(int ma_id, String caller);
	void auditMakeBase(int ma_id, String caller);
	void resAuditMakeBase(int ma_id, String caller);
	void approveMakeBase(int ma_id, String caller);
	void resApproveMakeBase(int ma_id, String caller);
	void submitMakeBase(int ma_id, String caller);
	void resSubmitMakeBase(int ma_id, String caller);
	void endMakeBase(int ma_id, String caller);
	void resEndMakeBase(int ma_id, String caller);
}
