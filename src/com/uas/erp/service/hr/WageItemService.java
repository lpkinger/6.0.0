package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

public interface WageItemService {
	void saveWageItem(String formStore, String caller);
	void updateWageItemById(String formStore, String caller);
	void deleteWageItem(int wg_id, String caller);
	void auditWageItem(int wg_id, String caller);
	void resAuditWageItem(int wg_id, String caller);
	void submitWageItem(int wg_id, String caller);
	void resSubmitWageItem(int wg_id, String caller);
	List<Map<String, Object>> getWageItems();
}
