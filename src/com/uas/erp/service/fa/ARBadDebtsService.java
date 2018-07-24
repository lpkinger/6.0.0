package com.uas.erp.service.fa;



public interface ARBadDebtsService {
	void saveARBadDebts(String formStore, String gridStore,String caller);
	void updateARBadDebtsById(String formStore, String gridStore,String caller);
	void deleteARBadDebts(int bd_id,String caller);
	void printARBadDebts(int bd_id,String caller);
	void auditARBadDebts(int bd_id,String caller);
	void resAuditARBadDebts(int bd_id,String caller );
	void submitARBadDebts(int bd_id,String caller);
	void resSubmitARBadDebts(int bd_id,String caller);
	void postARBadDebts(int bd_id,String caller);
	void resPostARBadDebts(int bd_id,String caller);
}
