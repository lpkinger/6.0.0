package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface CashFlowService {

	List<Map<String, Object>> cashFlowSum(String yearmonth);

	/**
	 * 
	 * @param yearmonth
	 * @param type
	 * @param catecode
	 * @return
	 */
	List<Map<String, Object>> getCashFlow(String yearmonth, String type, String catecode);

	String cashFlowSet(String caller, String data);

	void cleanInvalid(String yearmonth);

}
