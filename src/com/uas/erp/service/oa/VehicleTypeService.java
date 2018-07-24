package com.uas.erp.service.oa;


public interface VehicleTypeService {
	void saveVehicleType(String formStore, String  caller);
	void updateVehicleType(String formStore, String  caller);
	void deleteVehicleType(int vt_id, String  caller);
}
