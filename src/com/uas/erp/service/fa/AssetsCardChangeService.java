package com.uas.erp.service.fa;



public interface AssetsCardChangeService {
	void saveAssetsCardChange(String formStore, String gridStore, String caller);
	void updateAssetsCardChangeById(String formStore, String gridStore, String caller);
	void deleteAssetsCardChange(int acc_id, String caller);
	void auditAssetsCardChange(int acc_id, String caller);
	void submitAssetsCardChange(int acc_id, String caller);
	void resSubmitAssetsCardChange(int acc_id, String caller);
}
