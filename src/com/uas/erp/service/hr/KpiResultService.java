package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

public interface KpiResultService {
	List<Map<String, Object>> getKpiResult(String condition);
}
