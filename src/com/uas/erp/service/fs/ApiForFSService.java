package com.uas.erp.service.fs;

import java.util.List;
import java.util.Map;

public interface ApiForFSService {
	
	Map<String, Object> getFaReports(String yearmonths, Boolean exitUDStream, Boolean right);
	
	Map<String, Object> getDefaultDataS(Integer lastym, String applydate, Boolean financcondition, Boolean bankflow, Boolean productmix,
			Boolean updowncust, Boolean monetaryfund, Boolean accountinforar, Boolean accountinforothar, Boolean accountinforpp,
			Boolean accountinforinv, Boolean accountinforfix, Boolean accountinforlb, Boolean accountinforap, Boolean accountinforothap,
			Boolean accountinforlong);
	
	void recBalanceAssign(String mfcusts);
	
	void accountApply(String cqcode, String custcode, String custname, String amount);

	List<Map<String, Object>> getCustSaleReportProgress(String ordercode);
	
	Map<String, Object> getCustSaleReportDetail(String ordercode);
}
