package com.uas.erp.service.scm;

public interface MakeService {
	void saveMake(String formStore, String gridStore, String caller);
	void updateMakeById(String formStore, String gridStore, String caller);
	void deleteMake(int ma_id, String caller);
	void auditMake(int ma_id, String caller);
	void resAuditMake(int ma_id, String caller);
	void submitMake(int ma_id, String caller);
	void resSubmitMake(int ma_id, String caller);
	void bannedMake(int ma_id, String caller);
	void resBannedMake(int ma_id, String caller);
	String[] printMake(int ma_id, String caller,String reportName,String condition);
}
