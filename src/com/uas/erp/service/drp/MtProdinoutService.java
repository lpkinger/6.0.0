package com.uas.erp.service.drp;



public interface MtProdinoutService {
	
	void saveMtProdinout(String formStore, String gridStore,  String caller);
	
	void updateMtProdinoutById(String formStore, String gridStore,  String caller);
	
	void deleteMtProdinout(int mt_id,  String caller);
	
	void auditMtProdinout(int mt_id,  String caller);
	
	void resAuditMtProdinout(int mt_id,  String caller);
	
	void submitMtProdinout(int mt_id,  String caller);
	
	void resSubmitMtProdinout(int mt_id,  String caller);
	
	String maintainInToOut(String caller,String data);
}
