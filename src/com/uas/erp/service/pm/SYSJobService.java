package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface SYSJobService {
	void saveSYSJob(String formStore, String gridStore, String caller);
	void updateSYSJobById(String formStore, String gridStore, String caller);
	void deleteSYSJob(int sj_id, String caller);
	void auditSYSJob(int sj_id, String caller);
	void resAuditSYSJob(int sj_id, String caller);
	void submitSYSJob(int sj_id, String caller);
	void resSubmitSYSJob(int sj_id, String caller);
	void testOracleJob(int sj_id, String caller);
	void runOracleJob(int id, String caller);
	void enableOracleJob(int id, String caller);
	void stopOracleJob(int id, String caller);
	List<Map<String, Object>> getOracleJob(int start, int end);
	int getCountOracleJob();
	
	
}
