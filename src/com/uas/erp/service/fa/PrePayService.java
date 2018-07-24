package com.uas.erp.service.fa;



public interface PrePayService {
	void savePrePay(String caller ,String formStore, String gridStore,String param2,String param3);
	void updatePrePayById(String caller ,String formStore, String gridStore,String param2,String param3);
	void deletePrePay(String caller ,int pp_id);
	void printPrePay(String caller ,int pp_id);
	void auditPrePay(String caller ,int pp_id);
	void resAuditPrePay(String caller ,int pp_id);
	void submitPrePay(String caller ,int pp_id);
	void resSubmitPrePay(String caller ,int pp_id);

	void postPrePay(String caller ,int pp_id);
	void resPostPrePay(String caller ,int pp_id);
	String[] printPrePay(String caller,int pp_id,String reportName,String condition);

}
