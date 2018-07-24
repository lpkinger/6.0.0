package com.uas.erp.service.plm;

import java.util.Map;

public interface SampleMakeApplyService {
	void saveSampleMakeApply(String formStore, String caller);
	void updateSampleMakeApplyById(String formStore, String caller);
	void deleteSampleMakeApply(int id, String caller);
	void auditSampleMakeApply(int id, String caller);
	void submitSampleMakeApply(int id, String caller);
	void resSubmitSampleMakeApply(int id, String caller);
	void resAuditSampleMakeApply(int id, String caller);
	String turnApplication(int id,String caller);
	String turnMake(int id,String caller);
	String turnOtherOut(int id,String caller);
}
