package com.uas.erp.service.oa;


public interface VehiclearchivesService {
	

	void saveVehiclearchives(String formStore, String gridStore,String  caller);
	void updateVehiclearchivesById(String formStore, String gridStore,String  caller);
	
	void deleteVehiclearchives(int re_id, String  caller);

	void submitVehiclearchives(int id, String caller);

	void resSubmitVehiclearchives(int id, String caller);

	void auditVehiclearchives(int id, String caller);

	void resAuditVehiclearchives(int id, String caller);
	
	String checkVehiclearchives(int id, String caller);
	
}
 