package com.uas.erp.service.drp;



public interface SaleAskService {
	void deleteSaleAsk(int sa_id,  String caller);
	void saveSaleAsk(String formStore, String gridStore,  String caller);
	void updateSaleAsk(String formStore, String gridStore,  String caller);
	void auditSaleAsk(int sa_id,  String caller);
	void resAuditSaleAsk(int sa_id,  String caller);
	void submitSaleAsk(int sa_id,  String caller);
	void resSubmitSaleAsk(int sa_id,  String caller);
	String[] printSaleAsk(int sa_id,  String caller,String reportName,String condition);
	void endSaleAsk(int id,  String caller);
	void resEndSaleAsk(int id,  String caller);
	int turnSendNotify(int sa_id,  String caller);
	void getPrice(int sa_id);
}
