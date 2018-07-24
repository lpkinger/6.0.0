package com.uas.erp.service.scm;

public interface WarehouseManService {
	void updateWarehouseManById(String formStore, String gridStore);
	void clearWareMan(String caller, String condition);
}
