package com.uas.erp.service.fa;



public interface ExchangeGlService {
	/**
	 * 汇兑损益
	 * 
	 * @param yearmonth
	 * @param ca_code
	 * @param account
	 *            是否登帐
	 * @param data
	 *            CurrencysMonth
	 * @return
	 */
	String exchange(String yearmonth, String ca_code, Boolean account, String data);
}
