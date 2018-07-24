package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.LedgerFilter;

public interface LedgerDetailService {

	List<Map<String, Object>> getGLDetail(LedgerFilter filter);
}
