package com.uas.erp.service.wisdomPark;


public interface AgreementService {
	
	void saveAgreement(String caller, String formStore);
	
	void updateAgreement(String caller, String formStore);
	
	void deleteAgreement(String caller, int id);
	
	void publishAgreement(String caller, int id);
	
	void cancelAgreement(String caller, int id);
	
}