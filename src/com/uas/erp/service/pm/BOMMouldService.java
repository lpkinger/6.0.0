package com.uas.erp.service.pm;

public interface BOMMouldService {
	void saveBOMMould(String formStore, String gridStore, String caller);

	void updateBOMMouldById(String formStore, String gridStore, String caller);

	void deleteBOMMould(int app_id, String caller);

	void printBOMMould(int app_id, String caller);

	void auditBOMMould(int app_id, String caller);

	void resAuditBOMMould(int app_id, String caller);

	void submitBOMMould(int app_id, String caller);

	void resSubmitBOMMould(int app_id, String caller);

	void updateBOMMouldProcessing(String formStore, String param, String caller);

}
