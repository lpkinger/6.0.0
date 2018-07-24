package com.uas.erp.service.scm;

public interface QUABatchService {
	void auditQUABatch(int qba_id, String caller);
	void resAuditQUABatch(int qba_id, String caller);
	void submitQUABatch(int qba_id, String caller);
	void resSubmitQUABatch(int qba_id, String caller);
	void updateQUABatchById(String formStore, String gridStore, String caller);
	void saveQUABatch(String formStore, String gridStore, String caller);
	void deleteQUABatch(int qba_id, String caller);
}
