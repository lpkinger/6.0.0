package com.uas.erp.service.fa;



public interface AssetsIOService {

	void saveAssetsIO(String caller, String formStore, String gridStore);
	void deleteAssetsIO(String caller, int ai_id);
	void updateAssetsIOById(String caller, String formStore, String gridStore);
	void printAssetsIO(String caller, int ai_id);
	void auditAssetsIO(String caller, int ai_id);
	void resAuditAssetsIO(int ai_id, String caller);
	void submitAssetsIO(String caller, int ai_id);
	void resSubmitAssetsIO(int ai_id, String caller);

}
