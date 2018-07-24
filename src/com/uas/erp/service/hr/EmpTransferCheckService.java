package com.uas.erp.service.hr;

public interface EmpTransferCheckService {
	
	void save(String formStore, String gridStore, String caller);

	void updateEmpTransferCheckById(String formStore, String gridStore,String  caller);

	void deleteEmpTransferCheck(int id, String  caller);
	
	void auditEmpTransferCheck(int id, String caller);
	
	void resAuditEmpTransferCheck(int id, String caller);
	
	void submitEmpTransferCheck(int id, String caller) ;
	
	void resSubmitEmpTransferCheck(int id, String caller);

	void check(int id,String caller);
	
	void turnEmpTransferCheck(int id,String caller);
}
