package com.uas.erp.service.crm;



public interface RepairService {
	void saveRepair(String formStore,String caller);
	void deleteRepair(int re_id,String caller);
	void updateRepair(String formStore,String caller);
}
