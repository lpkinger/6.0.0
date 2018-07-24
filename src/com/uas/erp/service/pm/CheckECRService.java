package com.uas.erp.service.pm;

import java.util.Map;


public interface CheckECRService {
	void saveCheck(String formStore, String gridStore, String caller);
	void updateCheckById(String formStore, String gridStore, String caller);
	void deleteCheck(int ecr_id, String caller);
	void auditCheck(int ecr_id, String caller);
	void resAuditCheck(int ecr_id, String caller);
	void submitCheck(int ecr_id, String caller);
	void resSubmitCheck(int ecr_id, String caller);
	Map<String,Object> turnECN(int ecr_id, String caller);
	void endCheck(int ecr_id, String caller);
	void resEndCheck(int ecr_id, String caller);
}
