package com.uas.erp.service.hr;


public interface QuestionsetService {
	
	void saveQuestionset(String formStore, String caller);
	
	void updateQuestionsetById(String formStore, String caller);
	
	void deleteQuestionset(int or_id, String caller);
}
