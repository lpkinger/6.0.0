package com.uas.erp.service.oa;


public interface VehicleWorkDailyService {
	void saveVehicleWorkDaily(String formStore, String  caller);
	void updateVehicleWorkDaily(String formStore, String  caller);
	void deleteVehicleWorkDaily(int vwd_id, String  caller);
}
