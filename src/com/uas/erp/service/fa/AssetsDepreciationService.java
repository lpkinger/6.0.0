package com.uas.erp.service.fa;



public interface AssetsDepreciationService {

	void saveAssetsDepreciation(String caller, String formStore, String gridStore);

	void updateAssetsDepreciationById(String caller, String formStore, String gridStore);

	void deleteAssetsDepreciation(String caller, int de_id);

	String[] printAssetsDepreciation(String caller, int pu_id, String reportName,
			String condition);

	void auditAssetsDepreciation(String caller, int de_id);

	void resAuditAssetsDepreciation(String caller, int de_id);

	void submitAssetsDepreciation(String caller, int de_id);

	void resSubmitAssetsDepreciation(String caller, int de_id);

	void postAssetsDepreciation(String caller, int de_id);

	void resPostAssetsDepreciation(String caller, int de_id);
}
