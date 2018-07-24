package com.uas.erp.service.scm;

public interface MakeExpService {
	void saveMakeExp(String formStore, String gridStore, String caller);
	void updateMakeExpById(String formStore, String gridStore, String caller);
	void deleteMakeExp(int ma_id, String caller);
	void auditMakeExp(int ma_id, String caller);
	void resAuditMakeExp(int ma_id, String caller);
	void submitMakeExp(int ma_id, String caller);
	void resSubmitMakeExp(int ma_id, String caller);
	void bannedMakeExp(int ma_id, String caller);
	void resBannedMakeExp(int ma_id, String caller);
	String[] printMakeExp(int ma_id, String caller,String reportName,String condition);
	void getPOPrice(String me_code);
}
