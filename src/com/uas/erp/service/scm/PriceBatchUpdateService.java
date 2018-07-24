package com.uas.erp.service.scm;

public interface PriceBatchUpdateService {

	void savePriceBatchById(String formStore, String gridStore, String caller);

	void updatePriceBatchById(String formStore, String gridStore, String caller);

	void delete(int emid, String caller);

	void cleanFailed(int emid, String caller);

	void batchUpdateBill(int emid);

	void batchUpdateOutBill(int emid);

	void auditPriceBatch(String caller, int an_id);

	void resAuditPriceBatch(String caller, int an_id);

	void submitPriceBatch(String caller, int an_id);

	void resSubmitPriceBatch(String caller, int an_id);
}
