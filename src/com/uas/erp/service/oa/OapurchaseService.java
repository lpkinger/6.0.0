package com.uas.erp.service.oa;

import java.util.List;
import java.util.Map;

public interface OapurchaseService {
	
	void saveOapurchase(String formStore, String gridStore, String  caller);
	
	void updateOapurchaseById(String formStore, String gridStore, String  caller);
	
	void deleteOapurchase(int rp_id, String  caller);
	
	void auditOapurchase(int oa_id, String  caller);
	
	void resAuditOapurchase(int oa_id, String  caller);
	
	void submitOapurchase(int oa_id, String  caller);
	
	void resSubmitOapurchase(int oa_id, String  caller);
	
	void turnOaacceptance(String formdata,String griddata,String caller);

	String beatchturnOaacceptance(String  caller,
			 String data);

	List<String> getTurnOaacceptanceSql(Map<Object, Object> store,
			String griddata, String caller, String code);
	String[] printoaPurchase(int op_id, String  caller, String reportName, String condition);

	void endOapurchase(int op_id, String caller);

	void resEndOapurchase(int op_id, String caller);
}
