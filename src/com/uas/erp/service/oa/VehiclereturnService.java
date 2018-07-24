package com.uas.erp.service.oa;


public interface VehiclereturnService {
	void saveVehiclereturn(String formStore, String caller);

	void confirmVehiclereturn(int id, String caller);

	void resConfirmVehiclereturn(int id, String caller);
}
