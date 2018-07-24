package com.uas.erp.dao.common;

import net.sf.json.JSONObject;
import com.uas.erp.model.Employee;

public interface ReceivableDao {
	JSONObject turnBankRegister(String language, Employee employee);

	JSONObject turnBankRegister1(int dcrd_id, double dcrd_thisturnamount);
}
