package com.uas.erp.service.fs;

public interface LoadedPlanSetService {
	
	void saveLoadedPlanSet(String formStore, String param, String caller);
	
	void updateLoadedPlanSet(String formStore, String param, String caller);
	
	void deleteLoadedPlanSet(int id, String caller);

}
