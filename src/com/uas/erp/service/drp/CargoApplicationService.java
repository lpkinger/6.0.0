package com.uas.erp.service.drp;



public interface CargoApplicationService {

	void saveCargoApplication(String formStore, String param, 
			String caller);

	void updateCargoApplicationById(String formStore, String param,
			 String caller);

	void deleteCargoApplication(int id,  String caller);

	void submitCargoApplication(int id,  String caller);

	void resSubmitCargoApplication(int id,  String caller);

	void auditCargoApplication(int id,  String caller);

	void resAuditCargoApplication(int id,  String caller);

	int turnFXSale(int id,  String caller);

}
