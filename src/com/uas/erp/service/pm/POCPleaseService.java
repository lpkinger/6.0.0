package com.uas.erp.service.pm;


public interface POCPleaseService {
	void savePOCPlease(String formStore, String caller);
	void updatePOCPlease(String formStore, String caller);
	void deletePOCPlease(int poc_id, String caller);
	void auditPOCPlease(int poc_id, String caller);
	void resAuditPOCPlease(int poc_id, String caller);
	void submitPOCPlease(int poc_id, String caller);
	void resSubmitPOCPlease(int poc_id, String caller);
}
