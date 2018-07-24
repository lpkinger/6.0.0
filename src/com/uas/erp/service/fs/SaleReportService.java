package com.uas.erp.service.fs;

import java.util.Map;

public interface SaleReportService {
	
	Map<String, Object> getSaleReportData(String custcode, String ordercode, String action);
	
}
