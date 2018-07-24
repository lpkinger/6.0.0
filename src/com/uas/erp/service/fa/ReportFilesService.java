package com.uas.erp.service.fa;

public interface ReportFilesService {
	void saveReportFiles(String formStore, String caller);

	void updateReportFiles(String formStore, String caller);

	void deleteReportFiles(int id, String caller);

	void saveReportFilesG(String caller, String param);

	void deleteReportFilesG(String caller, int id);


}
