package com.uas.erp.service.crm;



public interface ComplaintService {
	void saveComplaint(String formStore,String caller);
	
	void updateComplaint(String formStore,String caller);
	
	void deleteComplaint(int co_id,String caller);
	
	void auditComplaint(int co_id, String  caller);

	void resAuditComplaint(int co_id, String  caller);

	void submitComplaint(int co_id, String  caller);

	void resSubmitComplaint(int co_id, String  caller);
}
