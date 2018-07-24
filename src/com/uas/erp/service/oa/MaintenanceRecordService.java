package com.uas.erp.service.oa;


public interface MaintenanceRecordService {
	void saveMaintenanceRecord(String formStore, String  caller);
	void updateMaintenanceRecord(String formStore, String  caller);
	void deleteMaintenanceRecord(int mr_id, String  caller);
}
