package com.uas.erp.service.fa;



public interface InsuBillService {

	void saveInsuBill(String caller, String formStore, String gridStore);
	void deleteInsuBill(String caller, int ib_id);
	void updateInsuBillById(String caller, String formStore, String gridStore);
	void printInsuBill(String caller, int ib_id);
	void auditInsuBill(String caller, int ib_id);
	void resAuditInsuBill(int ib_id, String caller);
	void submitInsuBill(String caller, int ib_id);
	void resSubmitInsuBill(int ib_id,String caller);

}
