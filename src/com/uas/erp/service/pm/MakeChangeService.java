package com.uas.erp.service.pm;

public interface MakeChangeService {
	void saveMakeChange( String caller,String formStore, String gridStore);
	void updateMakeChangeById(String caller,String formStore, String gridStore);
	void deleteMakeChange(String caller,int bo_id);
	void auditMakeChange(int bo_id,String caller);
	void resAuditMakeChange(String caller,int bo_id);
	void submitMakeChange(String caller,int bo_id);
	void resSubmitMakeChange(String caller,int bo_id);
}
