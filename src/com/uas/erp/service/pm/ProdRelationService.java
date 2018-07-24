package com.uas.erp.service.pm;


public interface ProdRelationService {
	void saveProdRelation(String formStore, String gridStore, String caller);
	void updateProdRelation(String formStore, String gridStore, String caller);
	void submitProdRelation(int id, String caller);
	void resSubmitProdRelation(int id, String caller);
	void auditProdRelation(int id, String caller);
	void resAuditProdRelation(int id, String caller);
	void deleteProdRelation(int id, String caller);
	String bannedProdRelation(String caller, String data);
}
