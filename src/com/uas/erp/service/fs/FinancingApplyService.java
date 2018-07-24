package com.uas.erp.service.fs;

import java.util.Map;

import javax.servlet.http.HttpSession;

public interface FinancingApplyService {
	
	Map<String, Object> getFinancingApply(String condition);

	Map<String, Object> submitApply(HttpSession session, String formStore, String caller);
	
	Map<String, Object> getFinancApplyProgress(String condition, String busincode);
	
}