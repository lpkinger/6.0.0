package com.uas.erp.service.hr;


public interface TrainmaterailService {
	
	void saveTrainmaterail(String formStore, String caller);
	
	void updateTrainmaterailById(String formStore, String caller);
	
	void deleteTrainmaterail(int or_id, String caller);
}
