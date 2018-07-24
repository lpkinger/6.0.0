package com.uas.erp.service.hr;


public interface QuestionService {
	
	void saveQuestion(String formStore, String caller);
	
	void updateQuestionById(String formStore, String caller);
	
	void deleteQuestion(int or_id, String caller);
}
