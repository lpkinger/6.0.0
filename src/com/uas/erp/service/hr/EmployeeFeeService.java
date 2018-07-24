package com.uas.erp.service.hr;


public interface EmployeeFeeService {
	void saveEmployeeFee(String formStore,String  caller);
	void deleteEmployeeFee(int ef_id, String  caller);
	void updateEmployeeFee(String formStore, String  caller);
	void updateBatchAssistRequire(String formStore,String gridStore, String  caller);
}
