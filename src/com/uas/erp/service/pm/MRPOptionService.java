package com.uas.erp.service.pm;


public interface MRPOptionService {
	void saveMRPOption(String formStore,String caller);
	void deleteMRPOption(int id, String caller);
	void updateMRPOption(String  formStore, String caller);
	void auditMRPOption(int id, String caller);
	void submitMRPOption(int id,String caller);
	void resSubmitMRPOption(int id, String caller);
	void resAuditMRPOption(int id, String caller);
}
