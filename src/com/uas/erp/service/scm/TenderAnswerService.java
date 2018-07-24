package com.uas.erp.service.scm;

import java.util.Map;

public interface TenderAnswerService {
	
	Map<String, Object> getTenderQuestionList(String page,String limit,String search,String date,String status);
	
	Map<String, Object> getTenderQuestion(String id);
	
	Map<String, Object> getQuestionsByTender(String tenderCode);

	void saveTenderAnswer(String caller, String formStore, String gridStore);
	
	void updateTenderAnswer(String caller, String formStore, String gridStore);
	
	void deleteTenderAnswer(String caller, int id);

	void auditTenderAnswer(int id, String caller);

	void resAuditTenderAnswer(int id, String caller);

	void submitTenderAnswer(int id, String caller);

	void resSubmitTenderAnswer(int id, String caller);
	
}
