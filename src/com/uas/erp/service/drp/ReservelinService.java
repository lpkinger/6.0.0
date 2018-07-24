package com.uas.erp.service.drp;



public interface ReservelinService {
	
	void saveReservelin(String formStore, String gridStore,  String caller);
	
	void updateReservelinById(String formStore, String gridStore,  String caller);
	
	void deleteReservelin(int pi_id,  String caller);
	
	void auditReservelin(int pi_id,  String caller);
	
	void resAuditReservelin(int pi_id,  String caller);
	
	void submitReservelin(int pi_id,  String caller);
	
	void resSubmitReservelin(int pi_id,  String caller);
	
}
