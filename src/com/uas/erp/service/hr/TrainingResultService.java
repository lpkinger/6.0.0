package com.uas.erp.service.hr;


public interface TrainingResultService {
	
	void saveTrainingResult(String formStore, String gridStore,String caller);
	
	void updateTrainingResultById(String formStore, String gridStore,String caller);
	
	void deleteTrainingResult(int id, String caller);
	
	void auditTrainingResult(int id, String caller);
	
	//void resAuditTrainingResult(int tp_id, String caller);
	
	void submitTrainingResult(int id, String caller);
	
	void resSubmitTrainingResult(int id, String caller);	
}
