package com.uas.erp.service.scm;

import org.apache.poi.ss.usermodel.Workbook;

public interface MCApplicationService {
	int turnPurchase(int ap_id, String caller);
	void getVendor(int[] id);
	String[] postApplication(int[] id, int ma_id_t);
	boolean ImportExcel(int id, Workbook wbs, String substring);
	void applicationdataupdate(int id, String caller);
	void saveMCApplication(String formStore, String param, String caller);
	void deleteMCApplication(int id, String caller);
	void updateMCApplicationById(String formStore, String param, String caller);
	String[] printMCApplication(int id, String caller, String reportName,
			String condition);
	void submitMCApplication(int id, String caller);
	void auditMCApplication(int id, String caller);
	void resAuditMCApplication(int id, String caller);
	void resSubmitMCApplication(int id, String caller);
}
