package com.uas.erp.service.fa;



public interface AssetsPleaseService {

	void saveAssetsPlease(String caller, String formStore, String gridStore);
	void deleteAssetsPlease(String caller, int ap_id);
	void updateAssetsPleaseById(String caller, String formStore, String gridStore);
	void printAssetsPlease(String caller, int ap_id);
	void auditAssetsPlease(String caller, int ap_id);
	void resAuditAssetsPlease(int ap_id, String caller);
	void submitAssetsPlease(String caller, int ap_id);
	void resSubmitAssetsPlease(int ap_id, String caller);
}
