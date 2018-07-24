package com.uas.erp.service.scm;

public interface VerifyApplyDetailService {
	void updateVerifyApplyDetailById(String formStore, String gridStore, String caller);

	void updateVerifyApplyDetailById2(String formStore, String gridStore1, String gridStore2, String caller);

	void deleteVerifyApplyDetail(int ve_id, String caller);

	void deleteVerifyApplyDetail2(int ve_id, String caller);

	String[] printVerifyApplyDetail(int ve_id, String reportName, String condition, String caller);

	void auditVerifyApplyDetail(int ve_id, String caller);

	void resAuditVerifyApplyDetail(int ve_id, String caller);

	void turnMrb(int id, String code, String caller);

	int turnMakeQualityYC(int id, String code, String caller);

	void catchProject(int veid, int prid);

	void cleanProject(int veid);

	void approveVerifyApplyDetail(int ve_id, String caller);

	void resApproveVerifyApplyDetail(int ve_id, String caller);

	void submitVerifyApplyDetail(int ve_id, String caller);

	void resSubmitVerifyApplyDetail(int ve_id, String caller);

	String SubpackageDetail(int vad_id, double tqty);

	String ClearSubpackageDetail(int vad_id);

	String PrintBarDetail(int vad_id);

	void updateQty(String data);

	void updateWhCodeInfo(String caller, String data);

	int turnProdAbnormal(int id, String caller);

	int turnT8DReport(int id, String caller);
	
	String InspectAgain(String ve_code,int ve_id);
}
