package com.uas.erp.service.pm;


public interface PreProductBOMService {
	void savePreProductBOM(String formStore, String caller);
	void updatePreProductBOMById(String formStore, String caller);
	void deletePreProductBOM(int pre_id, String caller);
	void auditPreProductBOM(int pre_id, String caller);
	void resAuditPreProductBOM(int pre_id, String caller);
	void submitPreProductBOM(int pre_id, String caller);
	void resSubmitPreProductBOM(int pre_id, String caller);
}
