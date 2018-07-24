package com.uas.erp.service.hr;

public interface ExamQuestService {
	void saveExamQuest(String formStore,String  caller);
	void deleteExamQuest(int eq_id, String  caller);
	void updateExamQuestById(String formStore,String  caller);
	void submitExamQuest(int eq_id, String  caller);
	void resSubmitExamQuest(int eq_id, String  caller);
	void auditExamQuest(int eq_id, String  caller);
	void resAuditExamQuest(int eq_id, String  caller);
}
