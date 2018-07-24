package com.uas.erp.service.hr;


public interface TrainsetService {
	
	void saveTrainset(String formStore, String caller);
	
	void updateTrainsetById(String formStore, String caller);
	
	void deleteTrainset(int ts_id, String caller);
}
