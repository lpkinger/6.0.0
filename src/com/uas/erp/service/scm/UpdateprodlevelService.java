package com.uas.erp.service.scm;

public interface UpdateprodlevelService {
	void saveUpdateprodlevel(String formStore, String gridStore);
	void updateUpdateprodlevelById(String formStore, String gridStore);
	void deleteUpdateprodlevel(int cp_id);
	void auditUpdateprodlevel(int cp_id);
	void resAuditUpdateprodlevel(int cp_id);
	void submitUpdateprodlevel(int cp_id);
	void resSubmitUpdateprodlevel(int cp_id);
}
