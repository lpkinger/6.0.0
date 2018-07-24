package com.uas.erp.service.pm;

public interface MouldFeePleaseService {
	void saveMouldFeePlease(String formStore, String gridStore, String caller);
	void updateMouldFeePleaseById(String formStore, String gridStore, String caller);
	void deleteMouldFeePlease(int id, String caller);
	String[] printMouldFeePlease(int id, String caller,String reportName,
			String condition);
	void auditMouldFeePlease(int id, String caller);
	void resAuditMouldFeePlease(int id, String caller);
	void submitMouldFeePlease(int id, String caller);
	void resSubmitMouldFeePlease(int id, String caller);
	void endMouldFeePlease(int id, String caller);
	void resEndMouldFeePlease(int id, String caller);
	int turnAccountRegister(int id, Object thisamount, String catecode, String thisdate, String caller);
	int turnBillAP(int id, Object thisamount, String catecode, String thisdate, String caller);
	int turnBillARChange(int id, Object thisamount, String catecode, String thisdate, String caller);
}
