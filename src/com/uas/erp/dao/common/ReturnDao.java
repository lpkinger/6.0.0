package com.uas.erp.dao.common;

import net.sf.json.JSONObject;
import com.uas.erp.model.Employee;

public interface ReturnDao {
	JSONObject turnBankRegister(String language, Employee employee);
	JSONObject turnBankRegister1(int ccrd_id, double ccrd_thisturnamount);
}
