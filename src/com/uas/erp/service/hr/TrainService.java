package com.uas.erp.service.hr;


public interface TrainService {
	
	void saveTrain(String formStore, String gridStore,String caller);
	
	void updateTrainById(String formStore, String gridStore,String caller);
	
	void deleteTrain(int tr_id, String caller);
	
	void auditTrain(int tr_id, String caller);
	
	void resAuditTrain(int tr_id, String caller);
	
	void submitTrain(int tr_id, String caller);
	
	void resSubmitTrain(int tr_id, String caller);
	
}
