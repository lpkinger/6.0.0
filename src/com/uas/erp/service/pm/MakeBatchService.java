package com.uas.erp.service.pm;

import com.uas.erp.model.Employee;

public interface MakeBatchService {
	void updateMakeBatchById(String formStore, String gridStore,String caller);
	void cleanMakeBatch(int mb_id,String caller);
	void batchToMake(int mb_id);
	String getcode(String caller, String table, int type, String conKind);
	void cleanFailed(int mb_id, String caller);
	void makeupdateDatalist(Employee employee, String caller, String data);
}
