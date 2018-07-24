package com.uas.erp.service.fa;

public interface DelegationLetterService {
	void saveDelegationLetter(String formStore, String caller);

	void updateDelegationLetter(String formStore, String caller);

	void deleteDelegationLetter(int dgl_id, String caller);

	void submitDelegationLetter(int dgl_id, String caller);

	void resSubmitDelegationLetter(int dgl_id, String caller);

	void auditDelegationLetter(int dgl_id, String caller);

	void resAuditDelegationLetter(int dgl_id, String caller);
	
	void endDelegationLetter(int dgl_id,String endreason, String caller);

	void resEndDelegationLetter(int dgl_id, String caller);

	String[] printReceiptDelegationLetter(int dgl_id, String caller, String reportName,
			String condition);

}
