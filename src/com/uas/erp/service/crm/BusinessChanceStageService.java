package com.uas.erp.service.crm;


public interface BusinessChanceStageService {

	void saveBusinessChanceStage(String formStore,String caller);
	void deleteBusinessChanceStage(int bs_id,String caller);
	void updateBusinessChanceStage(String formStore,String caller);
	void submitBusinessChanceStage(int bS_id,String caller);
	void resSubmitBusinessChanceStage(int bs_id,String caller);
	void auditBusinessChanceStage(int bs_id,String caller);
	void resAuditBusinessChanceStage(int bs_id,String caller);
}
