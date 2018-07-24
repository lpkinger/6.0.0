package com.uas.erp.service.fs;

import java.util.List;
import java.util.Map;

public interface CreditTargetsService {
	List<Map<String, Object>> getColItems();
	void saveCreditTargets(String formStore, String caller);
	void updateCreditTargets(String formStore, String caller);
	void deleteCreditTargets(int id, String caller);
	void submitCreditTargets(int id, String caller);
	void resSubmitCreditTargets(int id, String caller);
	void auditCreditTargets(int id, String caller);
	void resAuditCreditTargets(int id, String caller);
	
	void saveItemsValue(String datas);
	void deleteItemsValue(int id);
	void testSQL(String sql);
}
