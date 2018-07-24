package com.uas.erp.service.scm;

import java.util.Map;

public interface VendorPerformanceAssessService {
	void saveVPA(String caller, String formStore);

	void updateVPA(String caller, String formStore);

	void deleteVPA(String caller, int id);

	void auditVPA(int id,String caller);

	void resAuditVPA(String caller, int id);

	void submitVPA(String caller, int id);

	void resSubmitVPA(String caller, int id);
}
