package com.uas.erp.service.oa;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Sign;

public interface SignService {
	Sign getMySign(Employee employee);
	void signin(Employee employee, String reason);
	void signout(Employee employee, String reason);
}
