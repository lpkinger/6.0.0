package com.uas.erp.dao.common;

import com.uas.erp.model.Employee;

import net.sf.json.JSONObject;

public interface AskRepairDao {
	JSONObject turnRepairOrder(String language,Employee employee,String custcode, String repairman);
	
	void toRepairOrderDetail(String string, int crid, String language,
			Employee employee);
}
