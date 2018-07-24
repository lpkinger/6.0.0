package com.uas.erp.service.hr;


public interface KpiPositionService {
	void saveKpiPosition(String formStore, String gridStore,String caller);
	String show(String gridStore, String caller,String condition);
}
