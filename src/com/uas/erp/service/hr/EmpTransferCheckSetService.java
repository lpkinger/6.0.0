package com.uas.erp.service.hr;


public interface EmpTransferCheckSetService {
	
	void save(String formStore, String gridStore, String caller);

	void updateEmpTransferCheckSetById(String formStore, String gridStore,String  caller);

	void deleteEmpTransferCheckSet(int id, String  caller);

}
