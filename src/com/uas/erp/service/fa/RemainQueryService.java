package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface RemainQueryService {
	List<Map<String, Object>> RemainQuery(String condition);
}
