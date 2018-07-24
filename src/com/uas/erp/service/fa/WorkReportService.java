package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface WorkReportService {
	void saveWorkReport(String formStore, String gridStore, String caller);

	void updateWorkReportById(String formStore, String gridStore,
			String caller);

	void deleteWorkReport(int wr_id, String caller);

	void auditWorkReport(int wr_id, String caller);

	void resAuditWorkReport(int wr_id, String caller);

	void submitWorkReport(int wr_id, String caller);

	void resSubmitWorkReport(int wr_id, String caller);

	List<Map<String,Object>> getJobWork(String code);

}
