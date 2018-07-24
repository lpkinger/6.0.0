package com.uas.erp.service.plm;

public interface TaskResourceChangeService {
	void saveTaskResourceChange(String formStore);

	void deleteTaskResourceChange(int id);

	void updateTaskResourceChange(String formStore, String param);

	void auditTaskResourceChange(int id,String caller);

	void submitTaskResourceChange(int id);

	void resSubmitTaskResourceChange(int id);

	void resAuditTaskResourceChange(int id);

	void batchRescourceChange(String data);
}
