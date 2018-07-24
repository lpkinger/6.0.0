package com.uas.erp.service.hr;


public interface TrainexamService {
	
	void saveTrainexam(String formStore, String caller);
	
	void updateTrainexamById(String formStore, String caller);
	
	void deleteTrainexam(int or_id, String caller);
}
