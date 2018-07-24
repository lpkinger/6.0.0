package com.uas.erp.service.pm;

public interface BomSetService {
	void saveBomSet(String formStore, String gridStore,String caller);
	void updateBomSetById(String formStore,String gridStore, String caller);
	void deleteBomSet(int bs_id, String caller);
	void auditBomSet(int bs_id, String caller);
	void resAuditBomSet(int bs_id, String caller);
	void submitBomSet(int bs_id, String caller);
	void resSubmitBomSet(int bs_id, String caller);
}
