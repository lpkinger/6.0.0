package com.uas.erp.service.cost;

public interface ProductWHMonthAdjustService {
	void saveProductWHMonthAdjust(String formStore, String gridStore);

	void updateProductWHMonthAdjustById(String formStore, String gridStore);

	void deleteProductWHMonthAdjust(int pwa_id);

	String[] printProductWHMonthAdjust(int pwa_id, String reportName, String condition);

	void auditProductWHMonthAdjust(int pwa_id);

	void resAuditProductWHMonthAdjust(int pwa_id);

	void submitProductWHMonthAdjust(int pwa_id);

	void resSubmitProductWHMonthAdjust(int pwa_id);

	/**
	 * 期初调整单过账
	 * 
	 * @param pwa_id
	 * @param language
	 * @param employee
	 */
	void postProductWHMonthAdjust(int pwa_id);

	/**
	 * 期初调整单反过账
	 * 
	 * @param pwa_id
	 * @param language
	 * @param employee
	 */
	void resPostProductWHMonthAdjust(int pwa_id);
}
