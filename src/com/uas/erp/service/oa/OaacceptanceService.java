package com.uas.erp.service.oa;


public interface OaacceptanceService {
	
	void saveOaacceptance(String formStore, String gridStore,String caller);
	
	void updateOaacceptanceById(String formStore, String gridStore,String caller);
	
	void deleteOaacceptance(int op_id, String caller);
	
	void auditOaacceptance(int op_id,String caller);
	
	void resAuditOaacceptance(int op_id,String caller);
	
	void submitOaacceptance(int op_id,String caller);
	
	void resSubmitOaacceptance(int op_id,String caller);
	
	void turnOainstorage(String formdata,String griddata,String caller);

	void returnOainstorage(String formdata, String griddata, String caller);
	void ytPost(String formdata,String griddata,String caller);
	void ytResPost(String formdata, String griddata, String caller);
	String[] printOaacceptance(int op_id, String caller, String reportName, String condition);
	
	void postOaacceptance(int op_id, String caller);

	void resPostOaacceptance(int op_id, String caller);
}
