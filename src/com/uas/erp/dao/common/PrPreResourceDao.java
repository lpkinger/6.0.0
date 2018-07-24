package com.uas.erp.dao.common;

import com.uas.erp.model.Employee;

public interface PrPreResourceDao {

	int turnPreResource(int pr_id, String language, Employee employee);

}
