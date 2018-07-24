package com.uas.erp.service.fa;



public interface GoodsSendService {
	void turnGoodsSend();
	void saveGoodsSend(String caller ,String formStore, String gridStore);
	void updateGoodsSend(String caller ,String formStore, String gridStore);
	void deleteGoodsSend(String caller ,int gs_id);
	void printGoodsSend(String caller ,int gs_id);
	void auditGoodsSend(String caller ,int gs_id);
	void resAuditGoodsSend(String caller ,int gs_id);
	void submitGoodsSend(String caller ,int gs_id);
	void resSubmitGoodsSend(String caller ,int gs_id);
	void postGoodsSend(String caller ,int gs_id);
	void resPostGoodsSend(String caller ,int gs_id);
}
