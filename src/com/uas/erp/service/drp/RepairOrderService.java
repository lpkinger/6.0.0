package com.uas.erp.service.drp;



public interface RepairOrderService {
	
	void saveRepairOrder(String formStore, String gridStore,  String caller);
	
	void updateRepairOrderById(String formStore, String gridStore,  String caller);
	
	void deleteRepairOrder(int ro_id,  String caller);
	
	void auditRepairOrder(int ro_id,  String caller);
	
	void resAuditRepairOrder(int ro_id,  String caller);
	
	void submitRepairOrder(int ro_id,  String caller);
	
	void resSubmitRepairOrder(int ro_id,  String caller);

    String turnRepairWork(String caller, int roid);
    String batchCreateRepairOrder(String data,String caller);
    String batchTurnRepairWork(String data,String caller);
}
