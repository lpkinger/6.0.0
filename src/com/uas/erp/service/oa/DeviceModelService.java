package com.uas.erp.service.oa;

public interface DeviceModelService {
	
	void saveDeviceModel(String formStore,String param,String caller);

	void updateDeviceModel(String formStore,String param, String caller);

	void deleteDeviceModel(int dm_id, String caller);

	void auditDeviceModel(int dm_id, String caller);

	void resAuditDeviceModel(int dm_id, String caller);

	void submitDeviceModel(int dm_id, String caller);

	void resSubmitDeviceModel(int dm_id, String caller);
	
}
