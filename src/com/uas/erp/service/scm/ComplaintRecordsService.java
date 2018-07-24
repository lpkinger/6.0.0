package com.uas.erp.service.scm;

public interface ComplaintRecordsService {
	void saveComplaintRecords(String formStore, String caller);
	void updateComplaintRecordsById(String formStore, String caller);
	void deleteComplaintRecords(int cr_id, String caller);
	void auditComplaintRecords(int cr_id, String caller);
	void resAuditComplaintRecords(int cr_id, String caller);
	void submitComplaintRecords(int cr_id, String caller);
	void resSubmitComplaintRecords(int cr_id, String caller);
	void endComplaintRecords(int cr_id, String caller);
	void resEndComplaintRecords(int cr_id, String caller);
	void updateComplaint(int cr_id, String val1, String val2, String val3, String val4,String val0, String caller);
	String[] printComplaintRecords(int cr_id,String reportName,String condition, String caller);
}
