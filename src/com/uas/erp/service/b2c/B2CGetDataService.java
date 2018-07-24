package com.uas.erp.service.b2c;

import java.util.List;
import java.util.Map;

public interface B2CGetDataService {

	int getDatalistCount(String caller, String condition, String table, String fields);

	String getDatalistData(String caller, String condition, String table, String fields, String orderby, int page, int pageSize);

	List<Map<String,Object>> getFieldsDatas(String caller, String fields, String condition, String tablename);

}
