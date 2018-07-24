package com.uas.erp.service.ma;


public interface CurrencyService {
	Object getCurrencyDate();
	Object getSysCurrency();
	Object getBsCurrency();
	void saveCurrency(String formstore,String gridstore);
}
