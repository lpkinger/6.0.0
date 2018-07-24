package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;

public interface VendMonthDao {

	JSONObject turnPayBalance(String id, double balance, String language, Employee employee);
}
