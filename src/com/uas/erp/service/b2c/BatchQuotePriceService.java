package com.uas.erp.service.b2c;

import java.util.Map;

public interface BatchQuotePriceService {
	public Map<String,Object> quotePrice(String caller,String parameters);
	
	Map<String,Object> getCurrencyAndTaxrate(String caller,String code);
}
