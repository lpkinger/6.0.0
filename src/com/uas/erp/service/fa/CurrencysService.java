package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;



public interface CurrencysService {
	void saveCurrencys(String formStore, String caller);
	void updateCurrencysById(String formStore, String caller);
	void deleteCurrencys(int cr_id, String caller);
	void bannedCurrencys(int cr_id , String caller);
	void resBannedCurrencys(int cr_id , String caller);
	
	void updateCurrencysMonth(String gridStore,String mf, String caller);
	void deleteCurrencysMonth(String id, String caller);
	
	List<Map<String, Object>> getLastEndRate(String last, String caller);
	
}
