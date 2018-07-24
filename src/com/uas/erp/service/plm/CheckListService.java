package com.uas.erp.service.plm;

public interface CheckListService {
	void saveCheckList(String formStore, String gridStore);

	void deleteCheckList(int id);

	void updateCheckList(String formStore, String param);

	void auditCheckList(int id);

	void submitCheckList(int id);

	void reSubmitCheckList(int id);

	void resAuditCheckList(int id);
}
