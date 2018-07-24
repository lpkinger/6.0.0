package com.uas.erp.service.crm;

import com.uas.erp.model.Employee;

public interface TrainOrderService {
	void auditTrainOrder(int to_id, String language, Employee employee,String caller);
	void resAuditTrainOrder(int to_id, String language, Employee employee,String caller);
}
