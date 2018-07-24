package com.uas.erp.service.fa;



public interface AssetsKindService {
	void saveAssetsKind(String formStore, String gridStore, String caller);
	void updateAssetsKindById(String formStore, String gridStore, String caller);
	void deleteAssetsKind(int ak_id, String caller);
//	void printARBill(int pu_id);
	void auditAssetsKind(int ak_id, String caller);
	void resAuditAssetsKind(int ak_id, String caller);
	void submitAssetsKind(int ak_id, String caller);
	void resSubmitAssetsKind(int ak_id, String caller);
}
