package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface BarCodeService {
	List<Map<String, Object>> getBar(String codes);
}
