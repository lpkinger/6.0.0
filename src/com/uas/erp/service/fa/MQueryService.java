package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface MQueryService {
	List<Map<String, Object>> getMQuery(String condition);
	List<Map<String, Object>> getARDateQuery(String condition);
}
