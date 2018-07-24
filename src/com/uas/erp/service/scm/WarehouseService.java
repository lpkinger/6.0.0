package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface WarehouseService {

	void saveWarehouse(String caller, String formStore);

	void deleteWarehouse(String caller, int id);

	void updateWarehouse(String formStore, String caller);

	void submitWarehouse(int id, String caller);

	void resSubmitWarehouse(int id, String caller);

	void auditWarehouse(int id, String caller);

	void resAuditWarehouse(int id, String caller);

	List<Map<String, Object>> getWarehouse();

	Map<String, Object> updateIsMallStore(String param);
	
}
