package com.uas.erp.dao.common;

import com.uas.erp.model.Employee;

public interface ECRDao {
	int turnECN(int id, String language, Employee employee);
	
}
