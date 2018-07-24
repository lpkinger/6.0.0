package com.uas.erp.service.fa;



public interface CarryGlService {
	/**
	 * 结转损益
	 * 
	 * @param yearmonth
	 * @param ca_code
	 * @param account
	 *            是否登账
	 * @return
	 */
	String create(String yearmonth, String ca_code, Boolean account);
}
