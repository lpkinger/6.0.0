package com.uas.erp.service.fa;



public interface AmortProgramService {
	void saveAmortProgram(String formStore, String gridStore, String caller);
	void updateAmortProgramById(String formStore, String gridStore, String caller);
	void deleteAmortProgram(int ap_id, String caller);
	void auditAmortProgram(int ap_id, String caller);
	void resAuditAmortProgram(int ap_id, String caller);
}
