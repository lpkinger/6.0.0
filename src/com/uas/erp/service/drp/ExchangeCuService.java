package com.uas.erp.service.drp;



public interface ExchangeCuService {
	
	void saveExchangeCu(String formStore, String gridStore,  String caller);
	
	void updateExchangeCuById(String formStore, String gridStore,  String caller);
	
	void deleteExchangeCu(int ec_id,  String caller);
	
	void auditExchangeCu(int ec_id,  String caller);
	
	void resAuditExchangeCu(int ec_id,  String caller);
	
	void submitExchangeCu(int ec_id,  String caller);
	
	void resSubmitExchangeCu(int ec_id,  String caller);
	
}
