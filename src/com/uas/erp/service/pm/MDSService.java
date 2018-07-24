package com.uas.erp.service.pm;

public interface MDSService {
	void saveMDS(String formStore, String caller);
	void updateMDSById(String formStore, String param, String caller);
	void deleteMDS(int bo_id, String caller);
	void auditMDS(int bo_id, String caller);
	void resAuditMDS(int bo_id, String caller);
	void submitMDS(int bo_id, String caller);
	void resSubmitMDS(int bo_id, String caller);
	void deleteAllDetails(int id, String caller);

}
