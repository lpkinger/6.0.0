package com.uas.erp.service.drp;



public interface RepairWorkService {
	
	void saveRepairWork(String formStore, String gridStore,  String caller);
	
	void updateRepairWorkById(String formStore, String gridStore,  String caller);
	
	void deleteRepairWork(int rw_id,  String caller);
	
	void auditRepairWork(int rw_id,  String caller);
	
	void resAuditRepairWork(int rw_id,  String caller);
	
	void submitRepairWork(int rw_id,  String caller);
	
	void resSubmitRepairWork(int rw_id,  String caller);
	String batchTurnStockScrap(String data,String caller);
	String batchTurnAppropriationOut(String data,String caller);
	String TurnARBill(String caller,int id);
}
