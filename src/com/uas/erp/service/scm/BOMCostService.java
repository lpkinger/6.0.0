package com.uas.erp.service.scm;

public interface BOMCostService {
	void saveBOMCost(String formStore, String gridStore); 
	void updateBOMCostById(String formStore, String gridStore);
	void deleteBOMCost(int bc_id);
	void auditBOMCost(int bc_id);
	void resAuditBOMCost(int bc_id);
	void submitBOMCost(int bc_id);
	void resSubmitBOMCost(int bc_id);
	void bannedBOMCost(int bc_id);
	void resBannedBOMCost(int bc_id);
	String[] printBOMCost(int bc_id,String reportName,String condition);
	void bomInsert(int bc_id);
	void bomVastCost(int bc_id);
}
