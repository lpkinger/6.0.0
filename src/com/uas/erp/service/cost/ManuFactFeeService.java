package com.uas.erp.service.cost;

import com.uas.erp.model.Employee;

public interface ManuFactFeeService {
	void saveManuFactFee(String formStore, String language, Employee employee);
	void updateManuFactFee(String formStore, String language, Employee employee);
	void deleteManuFactFee(int mf_id, String language, Employee employee);
	void auditManuFactFee(int mf_id, String language, Employee employee);
	void resAuditManuFactFee(int mf_id, String language, Employee employee);
	void submitManuFactFee(int mf_id, String language, Employee employee);
	void resSubmitManuFactFee(int mf_id, String language, Employee employee);
}
