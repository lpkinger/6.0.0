package com.uas.erp.service.fa;



public interface BillAPChangeService {
	void saveBillAPChange(String formStore, String gridStore, String param2, String caller);
	void updateBillAPChangeById(String formStore, String gridStore, String param2, String caller);
	void deleteBillAPChange(int bpc_id, String caller);
	void printBillAPChange(int bpc_id, String caller);
	void auditBillAPChange(int bpc_id, String caller);
	void resAuditBillAPChange(int bpc_id, String caller);
	void submitBillAPChange(int bpc_id, String caller);
	void resSubmitBillAPChange(int bpc_id, String caller);
	void accountedBillAPChange(int bpc_id, String caller);
	void resAccountedBillAPChange(int bpc_id, String caller);
}
