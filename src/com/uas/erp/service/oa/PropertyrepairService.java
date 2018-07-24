package com.uas.erp.service.oa;


public interface PropertyrepairService {
	
	void savePropertyrepair(String formStore, String gridStore, String  caller);
	
	void updatePropertyrepairById(String formStore, String gridStore, String  caller);
	
	void deletePropertyrepair(int pr_id, String  caller);
	
	void auditPropertyrepair(int pr_id, String  caller);
	
	void resAuditPropertyrepair(int pr_id, String  caller);
	
	void submitPropertyrepair(int pr_id, String  caller);
	
	void resSubmitPropertyrepair(int pr_id, String  caller);
	
	void turnRepairRecords(int id,String griddata,String caller);
}
