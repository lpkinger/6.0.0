package com.uas.erp.service.oa;

public interface DeviceKindService {
	
	void saveDeviceKind(String formStore, String gridStore, String caller);

	void updateDeviceKindById(String formStore, String gridStore, String caller);

	void deleteDeviceKind(int dk_id, String caller);

}
