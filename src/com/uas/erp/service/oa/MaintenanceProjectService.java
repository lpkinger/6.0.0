package com.uas.erp.service.oa;


public interface MaintenanceProjectService {
	void saveMaintenanceProject(String formStore, String  caller);
	void updateMaintenanceProject(String formStore, String  caller);
	void deleteMaintenanceProject(int mp_id, String  caller);
}
