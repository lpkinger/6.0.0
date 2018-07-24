package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface LedgerMultiService {
	List<Map<String, Object>> getGeneralLedgerMulti(String condition);
}
