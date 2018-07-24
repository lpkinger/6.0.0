package com.uas.erp.dao.common;

import net.sf.json.JSONObject;
import com.uas.erp.model.Employee;

public interface CustMonthDao {
	JSONObject turnRecBalance(int id, Double balance, Double cm_prepayend, String language, Employee employee);
}
