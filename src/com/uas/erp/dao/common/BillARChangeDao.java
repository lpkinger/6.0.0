package com.uas.erp.dao.common;

import com.uas.erp.model.Employee;

public interface BillARChangeDao {
	void turnRecBalance(int id, String language, Employee employee);
	void turnPreRec(int id, String language, Employee employee);
	void turnPayBalance(int id, String language, Employee employee);
	void turnPrePay(int id, String language, Employee employee);
}
