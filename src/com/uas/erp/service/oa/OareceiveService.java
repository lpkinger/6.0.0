package com.uas.erp.service.oa;


public interface OareceiveService {
	
	void saveOareceive(String formStore, String gridStore, String  caller);
	
	void updateOareceiveById(String formStore, String gridStore, String  caller);
	
	void deleteOareceive(int rp_id, String  caller);
	
	void auditOareceive(int oa_id, String  caller);
	
	void resAuditOareceive(int oa_id, String  caller);
	
	void submitOareceive(int oa_id, String  caller);
	
	void resSubmitOareceive(int oa_id, String  caller);

	void getOaapplication(int or_id, String griddata,String  caller);
	void returnOaapplication(int or_id,String griddata,String  caller);
	String[] printOareceive(int or_id, String  caller, String reportName, String condition);

}
