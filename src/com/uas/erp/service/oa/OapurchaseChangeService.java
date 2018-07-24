package com.uas.erp.service.oa;


public interface OapurchaseChangeService {
	void saveOapurchaseChange(String formStore, String gridStore, String  caller);
	void deleteOapurchaseChange(int oc_id, String  caller);
	void updateOapurchaseChangeById(String formStore,String gridStore, String  caller);
	void submitOapurchaseChange(int oc_id, String  caller);
	void resSubmitOapurchaseChange(int oc_id, String  caller);
	void auditOapurchaseChange(int oc_id, String  caller);
}
