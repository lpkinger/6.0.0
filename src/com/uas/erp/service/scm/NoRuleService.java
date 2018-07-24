package com.uas.erp.service.scm;

public interface NoRuleService {

	 void saveNoRule(String caller, String formStore, String param);

	 void updateNoRule(String caller, String formStore, String param);

	 void deleteNoRule(String caller, int id);

	 void saveRuleMaxNum(String caller, String formStore, String param);

	 void updateRuleMaxNum(String caller, String formStore, String param);

	 void deleteRuleMaxNum(String caller, int id);
}
