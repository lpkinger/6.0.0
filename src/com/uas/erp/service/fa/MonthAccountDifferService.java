package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface MonthAccountDifferService {
	List<Map<String, Object>> getARDifferByCust(int yearmonth, String custcode, String currency, String catecode, boolean chkun);

	List<Map<String, Object>> getARDifferAll(int yearmonth, boolean chkun);

	List<Map<String, Object>> getAPDifferByVend(int yearmonth, String vendcode, String currency, String catecode, boolean chkun);

	List<Map<String, Object>> getAPDifferAll(int yearmonth, boolean chkun);

	List<Map<String, Object>> getGSDifferByCode(int yearmonth, String code, String currency, String type, boolean chkun);

	List<Map<String, Object>> getGSDifferAll(int yearmonth, boolean chkun);
	
	List<Map<String, Object>> getASDifferByCateCode(int yearmonth, String catecode, String type, boolean chkun);

	List<Map<String, Object>> getASDifferAll(int yearmonth, boolean chkun);

	List<Map<String, Object>> getCODifferByCode(int yearmonth, String type, boolean chkun);

	List<Map<String, Object>> getCODifferAll(int yearmonth, boolean chkun);

}
