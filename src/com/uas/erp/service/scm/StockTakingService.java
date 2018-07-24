package com.uas.erp.service.scm;

public interface StockTakingService {
	void updateStockTakingById(String formStore, String gridStore, String caller);
	void deleteStockTaking(int st_id, String caller);
	void auditStockTaking(int st_id, String caller);
	void resAuditStockTaking(int st_id, String caller);
}
