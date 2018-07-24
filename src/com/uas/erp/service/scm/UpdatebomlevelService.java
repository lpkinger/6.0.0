package com.uas.erp.service.scm;

public interface UpdatebomlevelService {
	void saveUpdatebomlevel(String formStore, String gridStore);
	void updateUpdatebomlevelById(String formStore, String gridStore);
	void deleteUpdatebomlevel(int ub_id);
	void auditUpdatebomlevel(int ub_id);
	void resAuditUpdatebomlevel(int ub_id);
	void submitUpdatebomlevel(int ub_id);
	void resSubmitUpdatebomlevel(int ub_id);
}
