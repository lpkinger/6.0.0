package com.uas.erp.service.crm;



public interface SolutionService {
	void saveSolution(String formStore,String caller);
	void deleteSolution(int so_id,String caller);
	void updateSolution(String formStore,String caller);
}
