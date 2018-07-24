package com.uas.erp.service.oa;

public interface DeviceChangeService {
	
	void saveDeviceChange(String formStore, String caller);

	void updateDeviceChange(String formStore, String caller);

	void deleteDeviceChange(int dc_id, String caller);

	void auditDeviceChange(int dc_id, String caller);

	void resAuditDeviceChange(int dc_id, String caller);

	void submitDeviceChange(int dc_id, String caller);

	void resSubmitDeviceChange(int dc_id, String caller);
	
	void confirmDeal(int dc_id, String caller);
	
	String turnScrap(int dc_id, String caller);
	
}
