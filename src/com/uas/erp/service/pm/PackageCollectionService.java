package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface PackageCollectionService {

	List<Map<String,Object>> loadQueryGridStore(String caller, String condition);

	Map<String,Object> generatePackage(double pa_totalqty, String pa_prodcode,
			String pr_id, String pa_makecode, String pa_outboxcode);

	List<Map<String,Object>> getPackageDetail(String condition);

	void clearPackageDetail(String caller, String outbox, String sncode);

	void updatePackageQty(String caller, String pa_outboxcode, long pa_totalqty);

	String printPackageSN(String caller, String pa_outboxcode, long lps_id);

	List<Map<String,Object>> getPrintTemplates(String caller, String condition);

}
