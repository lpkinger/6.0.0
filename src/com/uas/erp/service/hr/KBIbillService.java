package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;

public interface KBIbillService {
	void saveKBIbill(String formStore, String gridStore, String caller);
	void deleteKBIbill(int kb_id, String caller);
	void updateKBIbillById(String formStore,String gridStore, String caller);
	void submitKBIbill(int kb_id, String caller);
	void resSubmitKBIbill(int kb_id, String caller);
	void auditKBIbill(int kb_id, String caller);
	void resAuditKBIbill(int kb_id, String caller);
	void endKBIBill(String caller, String data);
	String showKbi(String condition);
	List<GridFields> getGridFields();
	List<GridColumns> getGridColumns();
	List<Object> getKeys();
	Map<Object, List<Object[]>> getAssessValue(String key);
}
