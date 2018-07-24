package com.uas.erp.service.drp;



public interface CargoApplicationCuService {
	
	void saveCargoApplicationCu(String formStore, String gridStore,  String caller);
	
	void updateCargoApplicationCuById(String formStore, String gridStore,  String caller);
	
	void deleteCargoApplicationCu(int ca_id,  String caller);
	
	void auditCargoApplicationCu(int ca_id,  String caller);
	
	void resAuditCargoApplicationCu(int ca_id,  String caller);
	
	void submitCargoApplicationCu(int ca_id,  String caller);
	
	void resSubmitCargoApplicationCu(int ca_id,  String caller);
	
}
