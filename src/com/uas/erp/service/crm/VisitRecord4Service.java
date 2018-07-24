package com.uas.erp.service.crm;



public interface VisitRecord4Service {
	void saveVisitRecord(String formStore, String[] gridStore,String caller);
	void updateVisitRecordById(String formStore, String[] gridStore,String caller);
	void deleteVisitRecord(int vr_id,String caller);
	String turnFeePlease(int vr_id,String caller);
}
