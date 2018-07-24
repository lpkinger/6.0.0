package com.uas.erp.service.crm;

import com.uas.erp.model.Employee;

public interface TrainReportService {
	void updateTrainReportById(String formStore, String language, Employee employee,String caller);
	void submitTrainReport(int tr_id, String language, Employee employee,String caller);
	void resSubmitTrainReport(int tr_id, String language, Employee employee,String caller);
	void auditTrainReport(int tr_id, String language, Employee employee,String caller);
	void resAuditTrainReport(int tr_id, String language, Employee employee,String caller);
}
