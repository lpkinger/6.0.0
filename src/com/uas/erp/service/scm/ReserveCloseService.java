package com.uas.erp.service.scm;

public interface ReserveCloseService {
	void reserveclose(Integer param);
	void unperiodsdetail(Integer param);
	/**
	 * 取期间
	 */
	int getCurrentYearmonth();
}
