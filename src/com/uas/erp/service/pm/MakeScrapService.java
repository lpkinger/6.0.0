package com.uas.erp.service.pm;

public interface MakeScrapService {
	
	void saveMakeScrap(String formStore, String gridStore, String caller);
	void updateMakeScrapById(String formStore, String gridStore, String caller);
	void deleteMakeScrap(int ms_id, String caller);
	void auditMakeScrap(int ms_id, String caller);
	void resAuditMakeScrap(int ms_id, String caller);
	void submitMakeScrap(int ms_id, String caller);
	void resSubmitMakeScrap(int ms_id, String caller);
	String[] printMakeScrap(int ms_id, String caller, String reportName,
			String condition);
}
