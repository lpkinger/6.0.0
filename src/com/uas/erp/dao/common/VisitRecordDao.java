package com.uas.erp.dao.common;

import com.uas.erp.model.Employee;

public interface VisitRecordDao {

	void turnReport(int vr_id, String language, Employee employee);

}
