package com.uas.erp.service.oa;


public interface DossierService {
	void saveDossier(String formStore,String   caller);
	void updateDossier(String formStore,String  caller);
	void deleteDossier(int id,String  caller);
}
