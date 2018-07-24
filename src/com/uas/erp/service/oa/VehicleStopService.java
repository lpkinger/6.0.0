package com.uas.erp.service.oa;


public interface VehicleStopService {
	void saveVehicleStop(String formStore, String  caller);
	void updateVehicleStop(String formStore, String  caller);
	void deleteVehicleStop(int vs_id, String  caller);
}
