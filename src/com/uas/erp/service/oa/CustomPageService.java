package com.uas.erp.service.oa;

public interface CustomPageService {
	void savePage(String caller, String formStore, String param);
	void updatePageById(String caller, String formStore, String param);
	void deletePage(String caller, int id);
	void printPage(String caller, int id);
	void auditPage( int id,String caller) throws Exception;
	void resAuditPage(String caller, int id);
	void submitPage(String caller, int id);
	void resSubmitPage(String caller, int id);
	void bannedPage(String caller ,int id );
	void resBannedPage(String caller,int id );
	void postPage(String caller, int id);
	void confirmPage(String caller, int id);
	void IfDatalist(String caller);
	void ToDataListByForm(String caller, String type);
	void orderByJprocess(String data,String caller);
	void confirm( int id,String caller);
	void resConfirm( int id,String caller);
	void turnPage(int id,String caller,String data);
	void submitApproves(String caller, int id);
	void resSubmitApproves(String caller, int id);
	
	void approvePage(int id, String caller);
	String turnBankRegister(int id, String paymentcode, String payment,
			double thispayamount);
	String turnDocPage(int id, String caller, String data);
	void endPage(String caller, int id);
	void resEndPage(String caller, int id);
	
	
}
