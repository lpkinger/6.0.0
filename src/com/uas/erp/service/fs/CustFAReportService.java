package com.uas.erp.service.fs;

public interface CustFAReportService {
	void saveCustFAReport(String formStore, String gridStore, String caller);

	void updateCustFAReport(String formStore, String gridStore, String caller);

	void deleteCustFAReport(int id, String caller);

	void count(int cr_id, String caller);
}
