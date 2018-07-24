package com.uas.erp.service.oa;

public interface DeviceInventoryService {
	void saveDeviceInventory(String formStore, String caller, String param);

	void updateDeviceInventoryById(String formStore, String caller, String param);

	void deleteDeviceInventory(int db_id, String caller);

	void auditDeviceInventory(int db_id, String caller);

	void resAuditDeviceInventory(int db_id, String caller);

	void submitDeviceInventory(int db_id, String caller);

	void resSubmitDeviceInventory(int db_id, String caller);
	
	void lossDeviceInventory(int db_id, String caller);
	
	void getDeviceAttribute(int db_id, String caller);
}
