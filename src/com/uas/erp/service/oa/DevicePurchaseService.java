package com.uas.erp.service.oa;

public interface DevicePurchaseService {
	
	void saveDevicePurchase(String formStore,String param,String caller);

	void updateDevicePurchase(String formStore,String param, String caller);

	void deleteDevicePurchase(int dp_id, String caller);

	void auditDevicePurchase(int dp_id, String caller);

	void resAuditDevicePurchase(int dp_id, String caller);

	void submitDevicePurchase(int dp_id, String caller);

	void resSubmitDevicePurchase(int dp_id, String caller);
	
}
