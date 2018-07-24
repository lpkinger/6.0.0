package com.uas.erp.service.fa;



public interface PrePaidService {
	void savePrePaid(String formStore, String gridStore, String caller);
	void updatePrePaidById(String formStore, String gridStore, String caller);
	void deletePrePaid(int pp_id, String caller);
	void auditPrePaid(int pp_id, String caller);
	void resAuditPrePaid(int pp_id, String caller);
	void postPrePaid(int pp_id, String caller);
	void resPostPrePaid(int pp_id, String caller);
}
