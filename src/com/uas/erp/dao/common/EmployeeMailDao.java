package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.EmployeeMail;

public interface EmployeeMailDao {
	List<EmployeeMail> getEmployeeMailByParentId(int parentid);
}
