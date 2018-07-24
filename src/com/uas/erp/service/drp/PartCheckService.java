package com.uas.erp.service.drp;



public interface PartCheckService {
	void savePartCheck(String formStore, String gridStore,  String caller);
	void deletePartCheck(int pc_id,  String caller);
	void updatePartCheckById(String formStore,String gridStore,  String caller);
	void submitPartCheck(int pc_id,  String caller);
	void resSubmitPartCheck(int pc_id,  String caller);
	void auditPartCheck(int pc_id,  String caller);
	void resAuditPartCheck(int pc_id,  String caller); 
	String batchTurnOtherIn(String data,String caller);
	String bathcTurnSaleReturn(String data,String caller);
	void confirmPartCheck(int id,  String caller);
}
