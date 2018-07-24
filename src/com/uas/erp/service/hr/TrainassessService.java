package com.uas.erp.service.hr;


public interface TrainassessService {
	
	void saveTrainassess(String formStore, String caller);
	
	void updateTrainassessById(String formStore, String caller);
	
	void deleteTrainassess(int  id, String caller);
	
	void auditTrainassess(int id, String caller);
		
	void submitTrainassess(int id, String caller);
	
	void resSubmitTrainassess(int id, String caller);
}
