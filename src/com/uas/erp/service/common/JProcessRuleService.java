package com.uas.erp.service.common;

import java.util.Map;

public interface JProcessRuleService {
	void saveJProcessRule(String caller,String formStore);
	void updateJProcessRule(String caller,String formStore);
	void deleteJProcessRule(int id,String caller);
	Map<String,Object> checkSql(String sql);
}
