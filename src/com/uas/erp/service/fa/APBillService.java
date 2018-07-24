package com.uas.erp.service.fa;

import net.sf.json.JSONObject;



public interface APBillService {
	void saveAPBill(String caller ,String formStore, String gridStore,String param2,String param3);
	void updateAPBillById(String caller ,String formStore, String gridStore,  String param2,String param3);
	void deleteAPBill(String caller ,int ab_id);
	String[] printAPBill(String caller,int id,String reportName,String condition);
	void auditAPBill(String caller ,int ab_id);
	void resAuditAPBill(String caller ,int ab_id);
	void submitAPBill(String caller ,int ab_id);
	void resSubmitAPBill(String caller ,int ab_id);
	void postAPBill(String caller ,int ab_id);
	void resPostAPBill(String caller ,int ab_id);
	void createVoucherAPO(String abcode, String abdate);
	String vastPostAPBill(String caller ,String data);
	void updateBillDate(Integer id, String date, String yearmonth);
	void confirmCheck(String caller ,int ab_id);
	void cancelCheck(String caller ,int ab_id);
	String[] printVoucherCodeAPBill(String caller, int ab_id,
			String reportName, String condition);
	void updateTaxcode(String caller, int ab_id, String ab_refno,
			String ab_remark);
	
	JSONObject copyAPBill(int id, String caller);
}
