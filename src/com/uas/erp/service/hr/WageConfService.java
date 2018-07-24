package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;


public interface WageConfService {
	List<Map<String, Object>> getOverWorkConf();

	Map<String, Object> getBaseConf();

	List<Map<String, Object>> getAbsenceConf();

	List<Map<String, Object>> getPersonTaxConf();

	void update(String formStore, String owgridStore, String ptgridStore, String abgridStore);


}
