package com.uas.erp.service.oa;


public interface VehicleuseService {
	
	void saveVehicleuse(String formStore, String  caller);
	
	void updateVehicleuseById(String formStore, String  caller);
	
	void deleteVehicleuse(int va_id, String  caller);
	
}
