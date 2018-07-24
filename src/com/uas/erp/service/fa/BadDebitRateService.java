package com.uas.erp.service.fa;



public interface BadDebitRateService {
	void updateBadDebitRateById(String formStore,String gridStore, String caller);
	void confirmBadDebtProvision(String data, String caller);
}
