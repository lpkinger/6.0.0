package com.uas.erp.service.hr;


public interface EmpWorkDateSpecialService {
	
	void saveEmpWorkDateSpecial(String formStore, String  caller);
	
	void updateEmpWorkDateSpecial(String formStore, String  caller);
	
	void deleteEmpWorkDateSpecial(int ews_id, String  caller);
	
	void auditEmpWorkDateSpecial(int ews_id, String  caller);
	
	void resAuditEmpWorkDateSpecial(int ews_id, String  caller);
	
	void submitEmpWorkDateSpecial(int ews_id, String  caller);
	
	void resSubmitEmpWorkDateSpecial(int ews_id, String  caller);
}
