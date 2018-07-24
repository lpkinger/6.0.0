package com.uas.erp.service.scm;

import org.apache.poi.ss.usermodel.Workbook;

public interface ApplicationService {
	void saveApplication(String formStore, String gridStore, String caller);
	void updateApplicationById(String formStore, String gridStore, String caller);
	void deleteApplication(int ap_id, String caller);
	void auditApplication(int ap_id, String caller);
	void resAuditApplication(int ap_id, String caller);
	void submitApplication(int ap_id, String caller);
	void resSubmitApplication(int ap_id, String caller);
	int turnPurchase(int ap_id, String caller);
	void getVendor(int[] id);
	void getMCVendor(int[] id);
	String[] postApplication(int[] id, int ma_id_t);
	String[] printApplication(int ap_id, String caller,String reportName,String condition);
	boolean ImportExcel(int id, Workbook wbs, String substring);
	void applicationdataupdate(int id, String caller);
	void updateQty(String data);
	Object ApplicationTurnMake(String caller,String data);
	void updateApplicationTurnMakeStatus(Object ma_adid);
	String postApplication(String caller,String data,String to);
	void getVendorByCaller(int[] id, String caller);
}
