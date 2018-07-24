package com.uas.erp.service.scm;

public interface VerifyApplyService {
	void saveVerifyApply(String formStore, String gridStore, String caller);

	void updateVerifyApply(String formStore, String gridStore, String caller);

	void deleteVerifyApply(int id, String caller);

	void auditVerifyApply(int id, String caller);

	void resAuditVerifyApply(int id, String caller);

	void submitVerifyApply(int id, String caller);

	void resSubmitVerifyApply(int id, String caller);

	String turnStorage(String caller, int va_id);

	String[] printVerifyApply(int va_id, String reportName, String condition, String caller);

	String detailTurnIQC(String data);

	String detailTurnFQC(String data);

	String Subpackage(int va_id);

	String ClearSubpackage(int va_id);

	String[] printBar(int va_id, String reportName, String condition);

	void ProduceBatch(int id, String caller);
	
	String generateBarcode(String caller, String formStore);
	
	void batchGenBarcode(String caller,String formStore);
	
	void saveBarcodeDetail(String caller,String gridStore);

	void deleteAllBarDetails(String caller,String id, int detno);

	void batchGenBO(String caller, String formStore);

	void splitVerifyApply(String formdata, String data, String caller);
	
}
