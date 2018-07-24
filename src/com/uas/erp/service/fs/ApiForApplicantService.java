package com.uas.erp.service.fs;

import java.util.List;
import java.util.Map;

public interface ApiForApplicantService {
	
	Boolean existSecret(String custname);
	
	Map<String, Object> assessFinancingApply(String FinancingApply,Integer year,String yearmonths);
	
	Map<String, Object> financingApply(String financingApply, String customer, String attaches, String customerExcutive, String shareholders, 
			String associateCompany, String changeInstruction, String mfCust, String businessCondition, String productMixe, 
			String updowncast, String financeCondition, String accountList);
	
	void AssignRecBalance(String cqcode, String sales, String custcode, String custname);
	
	List<Map<Object, Object>> FinancApplyProgress(String busincode);
	
	void AccountApplyFromB2B(String apply, Long faid, String mfcustname, String fsSales, String receipts, String attaches);
}
