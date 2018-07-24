package com.uas.erp.service.crm;

public interface VisitRecord3Service {
	void saveVisitRecord(String formStore, String[] gridStore, String caller);

	void updateVisitRecordById(String formStore, String[] gridStore,
			String caller);

	void deleteVisitRecord(int vr_id, String caller);

	void auditVisitRecord(int vr_id, String caller);

	void resAuditVisitRecord(int vr_id, String caller);

	void submitVisitRecord(int vr_id, String caller);

	void resSubmitVisitRecord(int vr_id, String caller);

	int autoSave(String vr_cuuu, String caller);

	String turnFeePlease(int vr_id, String caller);
}
