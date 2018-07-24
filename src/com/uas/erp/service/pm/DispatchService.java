package com.uas.erp.service.pm;

public interface DispatchService {
	void saveDispatch(String formStore, String gridStore, String caller);
	void updateDispatchById(String formStore, String gridStore, String caller);
	void deleteDispatch(int di_id, String caller);
	void printDispatch(int di_id, String caller);
	void auditDispatch(int di_id, String caller);
	void resAuditDispatch(int di_id, String caller);
	void submitDispatch(int di_id, String caller);
	void resSubmitDispatch(int di_id, String caller);
	int copyDispatch(int id, String caller);
	void selectDispatchByMakeCode(String makecode, Integer id);
}
