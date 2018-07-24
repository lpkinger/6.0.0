package com.uas.erp.service.oa;

public interface DeviceManageService {
	
	void saveDevice(String formStore, String caller, String param);

	void updateDeviceById(String formStore, String caller, String param);

	void deleteDevice(int de_id, String caller);

	void auditDevice(int de_id, String caller);

	void resAuditDevice(int de_id, String caller);

	void submitDevice(int de_id, String caller);

	void resSubmitDevice(int de_id, String caller);
	
	void vastMaintenanceDevice(String data,String caller);
}
