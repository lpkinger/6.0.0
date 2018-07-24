package com.uas.erp.service.oa;


public interface OaapplicationService {
	
	void saveOaapplication(String formStore, String gridStore, String  caller);
	
	void updateOaapplicationById(String formStore, String gridStore, String  caller);
	
	void deleteOaapplication(int rp_id, String  caller);
	
	void auditOaapplication(int oa_id, String  caller);
	
	void resAuditOaapplication(int oa_id, String  caller);
	
	void submitOaapplication(int oa_id, String  caller);
	
	void resSubmitOaapplication(int oa_id, String  caller);
	
	void turnOaPurchase(String formdata,String griddata,String  caller);
	String[] printOaapplication(int oa_id, String  caller, String reportName, String condition);

	void turnYPOut(String formdata, String griddata, String caller);
	String turnGoodPicking(String data,String caller);

	void endOaapplication(int oa_id, String caller);

	void resEndOaapplication(int oa_id, String caller);

}
