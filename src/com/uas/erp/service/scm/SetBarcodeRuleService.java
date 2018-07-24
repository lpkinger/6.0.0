package com.uas.erp.service.scm;

import java.util.Map;

public interface SetBarcodeRuleService {

	void saveRule(String formStore, String param, String caller);

	Map<String,Object> getData(String condition, String caller);

	void updateRule(String formStore, String param, String caller);

}
