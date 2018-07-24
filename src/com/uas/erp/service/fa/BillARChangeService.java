package com.uas.erp.service.fa;



public interface BillARChangeService {
	void saveBillARChange(String formStore, String gridStore, String param2, String caller);
	void updateBillARChangeById(String formStore, String gridStore, String param2, String caller);
	void deleteBillARChange(int brc_id, String caller);
	void printBillARChange(int brc_id, String caller);
	void auditBillARChange(int brc_id, String caller);
	void resAuditBillARChange(int brc_id, String caller);
	void submitBillARChange(int brc_id, String caller);
	void resSubmitBillARChange(int brc_id, String caller);
	void accountedBillARChange(int brc_id, String caller);
	void resAccountedBillARChange(int brc_id, String caller);
}
