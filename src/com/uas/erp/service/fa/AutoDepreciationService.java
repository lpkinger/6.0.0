package com.uas.erp.service.fa;

public interface AutoDepreciationService {
	void accrued(Integer param);

	/**
	 * 取期间
	 */
	int getCurrentYearmonth();

	int getCurrentYearmonthAR();

	int getCurrentYearmonthAP();

	int getCurrentYearmonthGL();

	int getCurrentYearmonthPLM();

	int getCurrentYearmonthGS();
}
